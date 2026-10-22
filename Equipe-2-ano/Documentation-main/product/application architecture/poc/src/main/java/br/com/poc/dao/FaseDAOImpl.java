package br.com.poc.dao;

import br.com.poc.model.Fase;
import br.com.poc.model.PontoDeRede;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implementação da interface FaseDAO usando JdbcTemplate para operações de banco de dados.
 * Fornece métodos para listar fases e carregar o layout de uma fase específica.
 *
 * A classe é anotada com @Repository para indicar que é um componente de acesso a dados.
 *
 * @author Nana
 */
@Repository
public class FaseDAOImpl implements FaseDAO {

    /** JdbcTemplate é usado para simplificar operações de banco de dados JDBC.
     * Ele lida com a criação e liberação de recursos, além de fornecer métodos convenientes para executar consultas e atualizações.
     */
    private final JdbcTemplate jdbcTemplate;

    /** Construtor que injeta o JdbcTemplate (usa Injeção de Dependência: em vez de criar um JdbcTemplate, ele recebe pronto do Spring).
     *
     * @param jdbcTemplate - Instância do JdbcTemplate para operações de banco de dados.
     */
    public FaseDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Lista todas as fases do banco de dados.
     * Usa JdbcTemplate para executar a consulta SQL.
     * Mapeia cada linha do resultado para um objeto Fase.
     *
     * @return List<Fase> - Lista de todas as fases.
     */
    @Override
    public List<Fase> listarTodas() {
        String sql = "SELECT id, nome, bloqueada FROM fase ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Fase(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getBoolean("bloqueada")
        ));
    }

    /**
     * Carrega o layout de uma fase específica, retornando os pontos de rede associados.
     * Usa JdbcTemplate para executar a consulta SQL.
     * Mapeia cada linha do resultado para um objeto PontoDeRede.
     *
     * @param faseId - ID da fase cujo layout deve ser carregado.
     * @return List<PontoDeRede> - Lista de pontos de rede da fase especificada.
     */
    @Override
    public List<PontoDeRede> carregarLayoutFase(long faseId) {
        String sql = "SELECT id, tipo, pos_x, pos_y FROM ponto_de_rede WHERE fase_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new PontoDeRede(
                rs.getLong("id"),
                rs.getString("tipo"),
                rs.getDouble("pos_x"),
                rs.getDouble("pos_y")
        ), faseId);
    }
}
