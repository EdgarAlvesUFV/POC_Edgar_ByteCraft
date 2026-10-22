package app.repository;

import app.model.Sessao;
import app.model.StatusSessao;
import app.model.Turma;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SessaoRepository {

    private final JdbcTemplate jdbcTemplate;

    // RowMapper para converter em um objeto Sessao
    private static final RowMapper<Sessao> SESSAO_RM = (rs, i) -> {
        Sessao sessao = new Sessao();
        sessao.setIdSessao(rs.getLong("id_sessao"));
        sessao.setStatusSessao(StatusSessao.valueOf(rs.getString("status_sessao")));

        // Mapeia a chave estrangeira para um objeto Turma
        Turma turma = new Turma();
        turma.setId(rs.getLong("turma_id"));
        sessao.setTurma(turma);

        return sessao;
    };

    // Salva uma nova sessão no banco de dados
    public Sessao salvarSessao(Sessao sessao) {
        String sql = """
                INSERT INTO sessoes (turma_id, status_sessao)
                VALUES (?, ?)
        """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, sessao.getTurma().getId());
            ps.setString(2, sessao.getStatusSessao().name());
            return ps;
        }, keyHolder);

        // atribui a chave gerada de volta ao objeto original
        Optional.ofNullable(keyHolder.getKey())
                .ifPresent(key -> sessao.setIdSessao(key.longValue()));

        return sessao;
    }

    // Buscar uma sessão pelo seu Id
    public Optional<Sessao> acharPeloId(Long id) {
        String sql = """
                SELECT id_sessao, turma_id, status_sessao
                FROM sessoes
                WHERE id_sessao = ?
        """;
        try {
            Sessao sessao = jdbcTemplate.queryForObject(sql, SESSAO_RM, id);
            return Optional.ofNullable(sessao);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Método que atualiza o status da sessão existente no banco de dados
    public void atualizarStatusSessao(Sessao sessao) {
        String sql = """
                UPDATE sessoes
                SET status_sessao = ?
                WHERE id_sessao = ?
        """;
        jdbcTemplate.update(sql, sessao.getStatusSessao().name(), sessao.getIdSessao());
    }
}
