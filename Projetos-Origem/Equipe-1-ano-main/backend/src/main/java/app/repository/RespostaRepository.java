package app.repository;

import app.model.Misterio;
import app.model.Resposta;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RespostaRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Resposta> RESPOSTA_RM = (rs, rowNum) -> {
        Resposta resposta = new Resposta();
        resposta.setId(rs.getLong("id"));
        resposta.setDescricao(rs.getString("descricao"));
        resposta.setOrdem(rs.getInt("ordem"));
        resposta.setTipo(rs.getString("tipo")); // ← banco usa 'tipo', objeto usa 'tipo'
        resposta.setStatus(rs.getBoolean("status"));

        Misterio misterio = new Misterio();
        misterio.setId(rs.getLong("misterio_id"));
        resposta.setMisterioID(misterio);

        return resposta;
    };

    public List<Resposta> alternativasMisterioID(Misterio misterio) {
        String sql = """
            SELECT id, descricao, ordem, tipo, status, misterio_id 
            FROM respostas 
            WHERE misterio_id = ?
        """;
        return jdbcTemplate.query(sql, RESPOSTA_RM, misterio.getId());
    }

    public List<Resposta> buscaIncorretas(int limite) {
        String sql = """
            SELECT r.id, r.descricao, r.ordem, r.tipo, r.status, r.misterio_id
            FROM respostas r
            WHERE r.status = false
            ORDER BY RANDOM()
            LIMIT ?
        """;
        return jdbcTemplate.query(sql, RESPOSTA_RM, limite);
    }

    public Resposta buscaCorretaMisterio(Long misterioID) {
        String sql = """
            SELECT r.id, r.descricao, r.ordem, r.tipo, r.status, r.misterio_id
            FROM respostas r
            WHERE r.misterio_id = ? AND r.status = true
            LIMIT 1
        """;
        return jdbcTemplate.queryForObject(sql, RESPOSTA_RM, misterioID);
    }
}
