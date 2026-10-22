package app.repository;

import app.model.PistaUso;
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
public class PistaUsoRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JdbcTemplate plainJdbc;

    private static final RowMapper<PistaUso> PISTA_USO_ROW_MAPPER = (rs, rowNum) -> {
        PistaUso pu = new PistaUso();
        pu.setId(rs.getLong("id"));
        pu.setProgressoId(rs.getLong("progresso_id"));
        pu.setSessaoMisterioId(rs.getLong("sessao_misterio_id"));
        pu.setPistaId(rs.getLong("pista_id"));
        pu.setOrdem(rs.getInt("ordem"));
        pu.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
        pu.setTipoPista(rs.getString("tipo_pista"));
        return pu;
    };

    public Long inserirPistaUso(Long progressoId,
                                Long sessaoMisterioId,
                                Long pistaId,
                                Integer ordem,
                                String tipoPista) {

        final String sql = """
            INSERT INTO pistauso 
                (progresso_id, sessao_misterio_id, pista_id, ordem, data_hora, tipo_pista)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        KeyHolder kh = new GeneratedKeyHolder();

        plainJdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, progressoId);
            ps.setObject(2, sessaoMisterioId);
            ps.setObject(3, pistaId);
            ps.setObject(4, ordem);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(6, tipoPista);
            return ps;
        }, kh);

        return kh.getKey() != null ? kh.getKey().longValue() : null;
    }

    public List<PistaUso> buscarListaProgressoID(List<Long> progressoIDs) {
        if (progressoIDs == null || progressoIDs.isEmpty()) {
            return Collections.emptyList();
        }

        final String sql = """
            SELECT * FROM pistauso
            WHERE progresso_id IN (:ids)
        """;

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", progressoIDs);

        return jdbcTemplate.query(sql, parameters, PISTA_USO_ROW_MAPPER);
    }
}
