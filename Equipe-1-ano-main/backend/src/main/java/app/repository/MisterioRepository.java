package app.repository;

import app.model.Misterio;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MisterioRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Misterio> MISTERIO_RM = (rs, i) -> {
        Misterio misterio = new Misterio();
        misterio.setId(rs.getLong("id"));
        misterio.setTitulo(rs.getString("titulo"));
        return misterio;
    };

    // --------------------------
    // CRUD BÁSICO
    // --------------------------
    public List<Misterio> todosMisterios() {
        String sql  = """
            SELECT id, titulo
            FROM misterios
        """;
        return jdbcTemplate.query(sql, MISTERIO_RM);
    }

    public List<Misterio> sorteiaMisterio(int limite) {
        String sql = """
            SELECT id, titulo
            FROM misterios
            ORDER BY RAND() LIMIT ?
        """;
        return jdbcTemplate.query(sql, MISTERIO_RM, limite);
    }

    public Optional<Misterio> buscaMisterio(Long id) {
        String sql = """
            SELECT id, titulo
            FROM misterios
            WHERE id = ?
        """;
        try {
            Misterio misterio = jdbcTemplate.queryForObject(sql, MISTERIO_RM, id);
            return Optional.ofNullable(misterio);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Misterio> proximoMisterio(Long id) {
        String sql = """
            SELECT id, titulo
            FROM misterios
            WHERE id > ?
            ORDER BY id ASC LIMIT 1
        """;
        try {
            Misterio misterio = jdbcTemplate.queryForObject(sql, MISTERIO_RM, id);
            return Optional.ofNullable(misterio);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM misterios";
        Long total = jdbcTemplate.queryForObject(sql, Long.class);
        return (total != null) ? total : 0;
    }

    // --------------------------
    // RELAÇÃO SESSÃO ↔ MISTÉRIO
    // --------------------------

    /**
     * Retorna todos os mistérios vinculados a uma sessão
     */
    public List<Misterio> buscarPorSessao(Long sessaoId) {
        String sql = """
            SELECT m.id, m.titulo
            FROM misterios m
            INNER JOIN sessao_misterios sm ON m.id = sm.misterio_id
            WHERE sm.sessao_id = ?
        """;
        return jdbcTemplate.query(sql, MISTERIO_RM, sessaoId);
    }

    /**
     * Vincula um mistério a uma sessão
     */
    public Long adicionarMisterioNaSessao(Long sessaoId, Long misterioId) {
        String sql = """
        INSERT INTO sessao_misterios (sessao_id, misterio_id)
        VALUES (?, ?)
    """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, sessaoId);
            ps.setLong(2, misterioId);
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }


    /**
     * Remove o vínculo entre mistério e sessão
     */
    public void removerMisterioDaSessao(Long sessaoId, Long misterioId) {
        String sql = """
            DELETE FROM sessao_misterios
            WHERE sessao_id = ? AND misterio_id = ?
        """;
        jdbcTemplate.update(sql, sessaoId, misterioId);
    }

    /**
     * Remove todos os vínculos de uma sessão (resetar a sessão)
     */
    public void limparMisteriosDaSessao(Long sessaoId) {
        String sql = """
            DELETE FROM sessao_misterios
            WHERE sessao_id = ?
        """;
        jdbcTemplate.update(sql, sessaoId);
    }
}
