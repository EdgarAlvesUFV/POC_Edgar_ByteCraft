package app.repository;

import app.model.Misterio;
import app.model.Pista;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PistaRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Pista> PISTA_RM = (rs, rowNum) -> {
        Pista pista = new Pista();
        pista.setId(rs.getLong("id"));
        pista.setConteudo(rs.getString("conteudo"));
        pista.setTipo(rs.getString("tipo"));

        Misterio misterio = new Misterio();
        misterio.setId(rs.getLong("misterio_id"));
        pista.setMisterioID(misterio);

        return pista;
    };

    /**
     * Retorna todas as pistas vinculadas a um mistério
     */
    public List<Pista> retornaPistasVinculadasMisterio(Misterio misterio) {
        final String sql = """
            SELECT id, conteudo, tipo, misterio_id
            FROM pistas
            WHERE misterio_id = ?
        """;
        return jdbcTemplate.query(sql, PISTA_RM, misterio.getId());
    }

    /**
     * Busca uma pista específica pelo ID
     */
    public Optional<Pista> buscarPorId(Long id) {
        final String sql = """
            SELECT id, conteudo, tipo, misterio_id
            FROM pistas
            WHERE id = ?
        """;
        try {
            Pista pista = jdbcTemplate.queryForObject(sql, PISTA_RM, id);
            return Optional.ofNullable(pista);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Insere uma nova pista vinculada a um mistério
     */
    public void salvar(Pista pista) {
        final String sql = """
            INSERT INTO pistas (conteudo, tipo, misterio_id)
            VALUES (?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                pista.getConteudo(),
                pista.getTipo(),
                pista.getMisterioID().getId());
    }

    /**
     * Exclui uma pista pelo ID
     */
    public void deletar(Long id) {
        final String sql = "DELETE FROM pistas WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
