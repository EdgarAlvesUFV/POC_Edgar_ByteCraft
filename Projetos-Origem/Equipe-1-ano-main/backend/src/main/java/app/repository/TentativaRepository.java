package app.repository;

import app.model.Tentativa;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TentativaRepository {

    // já existia
    private final NamedParameterJdbcTemplate jdbcTemplate;

    // 🔹 novo: vamos precisar de insert com RETURN_GENERATED_KEYS
    private final JdbcTemplate plainJdbc;

    private static final RowMapper<Tentativa> TENTATIVA_ROW_MAPPER = (rs, rowNum) -> {
        Tentativa t = new Tentativa();
        t.setId(rs.getLong("id"));
        t.setProgressoId(rs.getLong("progressoId"));
        t.setMisterioId(rs.getLong("misterioId"));
        t.setAlternativaEscolhida(rs.getString("alternativaEscolhida"));
        t.setCorreta(rs.getBoolean("correta"));
        t.setTempoRespostaMs(rs.getLong("tempoRespostaMs"));
        t.setDataHora(rs.getTimestamp("dataHora").toLocalDateTime());
        return t;
    };

    /**
     * ✅ novo
     * Insere uma tentativa ligada a um progresso.
     */
    public Long inserirTentativa(Long progressoId,
                                 Long misterioId,
                                 Long respostaId,
                                 boolean correta,
                                 Long tempoRespostaMs) {

        final String sql = """
                INSERT INTO Tentativa
                    (progressoId, misterioId, alternativaEscolhida, correta, tempoRespostaMs, dataHora)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        KeyHolder kh = new GeneratedKeyHolder();

        plainJdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, progressoId);
            ps.setLong(2, misterioId);
            ps.setString(3, respostaId != null ? respostaId.toString() : null);
            ps.setBoolean(4, correta);
            ps.setLong(5, tempoRespostaMs != null ? tempoRespostaMs : 0L);
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, kh);

        return kh.getKey() != null ? kh.getKey().longValue() : null;
    }

    /**
     * já existia
     * Busca todas as tentativas associadas a uma lista de IDs de Progresso.
     */
    public List<Tentativa> buscarListaProgressoID(List<Long> progressoIDs) {
        if (progressoIDs == null || progressoIDs.isEmpty()) {
            return Collections.emptyList();
        }

        final String sql = """
            SELECT * FROM Tentativa
            WHERE progressoId IN (:ids)
        """;

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", progressoIDs);

        return jdbcTemplate.query(sql, parameters, TENTATIVA_ROW_MAPPER);
    }
}
