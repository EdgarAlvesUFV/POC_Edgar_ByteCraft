package app.repository;

import app.model.Progresso;
import lombok.RequiredArgsConstructor;
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
public class ProgressoRepository {

    private final JdbcTemplate jdbcTemplate;

    // ✅ RowMapper compatível com os nomes camelCase do banco
    private static final RowMapper<Progresso> PROGRESSO_ROW_MAPPER = (rs, rowNum) -> {
        Progresso p = new Progresso();
        p.setId(rs.getLong("id"));
        p.setSessaoMisterioId(rs.getLong("sessao_misterio_id"));
        p.setPontuacaoObtida(rs.getInt("pontuacaoObtida"));
        p.setTempoResolucaoMs(rs.getLong("tempoResolucaoMs"));
        p.setResolvido(rs.getBoolean("resolvido"));
        return p;
    };

    // ✅ Busca progresso por ID de sessão-mistério
    public Optional<Progresso> buscarPorSessaoMisterio(Long sessaoMisterioId) {
        final String sql = """
            SELECT * FROM progresso
            WHERE sessao_misterio_id = ?
        """;
        var lista = jdbcTemplate.query(sql, PROGRESSO_ROW_MAPPER, sessaoMisterioId);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    // ✅ Busca o id do relacionamento sessao_misterio
    public Long buscarSessaoMisterioId(Long sessaoId, Long misterioId) {
        final String sql = """
            SELECT id FROM sessao_misterios
            WHERE sessao_id = ? AND misterio_id = ?
            LIMIT 1
        """;
        var result = jdbcTemplate.queryForList(sql, Long.class, sessaoId, misterioId);
        return result.isEmpty() ? null : result.get(0);
    }

    // ✅ Inserção com os nomes de coluna corretos
    public Long inserir(Progresso p) {
        final String sql = """
        INSERT INTO progresso (sessao_misterio_id, pontuacaoObtida, tempoResolucaoMs, resolvido)
        VALUES (?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            pontuacaoObtida = VALUES(pontuacaoObtida),
            tempoResolucaoMs = VALUES(tempoResolucaoMs),
            resolvido = VALUES(resolvido)
    """;

        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, p.getSessaoMisterioId());
            ps.setInt(2, p.getPontuacaoObtida());
            ps.setLong(3, p.getTempoResolucaoMs());
            ps.setBoolean(4, p.isResolvido());
            return ps;
        }, kh);

        return kh.getKey() != null ? kh.getKey().longValue() : null;
    }


    // ✅ Atualização com as colunas em camelCase
    public void atualizar(Progresso p) {
        final String sql = """
            UPDATE progresso
               SET pontuacaoObtida = ?,
                   tempoResolucaoMs = ?,
                   resolvido = ?
             WHERE sessao_misterio_id = ?
        """;

        jdbcTemplate.update(sql,
                p.getPontuacaoObtida(),
                p.getTempoResolucaoMs(),
                p.isResolvido(),
                p.getSessaoMisterioId());
    }

    // ✅ Busca todos os progressos de uma sessão específica
    public List<Progresso> buscarPorSessao(Long sessaoId) {
        final String sql = """
            SELECT p.*
              FROM progresso p
              INNER JOIN sessao_misterios sm ON p.sessao_misterio_id = sm.id
             WHERE sm.sessao_id = ?
        """;
        return jdbcTemplate.query(sql, PROGRESSO_ROW_MAPPER, sessaoId);
    }

    // ✅ Busca o mistério vinculado a um sessao_misterio específico
    public Long buscarMisterioIdPorSessaoMisterio(Long sessaoMisterioId) {
        final String sql = "SELECT misterio_id FROM sessao_misterios WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, sessaoMisterioId);
    }
}
