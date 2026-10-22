package br.com.poc.dao;

import br.com.poc.model.Aluno;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

/**
 * Implementação da interface AlunoDAO usando JdbcTemplate para operações de banco de dados.
 * Fornece métodos para buscar e salvar alunos.
 *
 * A classe é anotada com @Repository para indicar que é um componente de acesso a dados.
 *
 * @author Nana
 */
@Repository
public class AlunoDAOImpl implements AlunoDAO {

    /** JdbcTemplate é usado para simplificar operações de banco de dados JDBC.
     * Ele lida com a criação e liberação de recursos, além de fornecer métodos convenientes para executar consultas e atualizações.
     */
    private final JdbcTemplate jdbcTemplate;

    /** Construtor que injeta o JdbcTemplate (usa Injeção de Dependência: em vez de criar um JdbcTemplate, ele recebe pronto do Spring).
     *
     * @param jdbcTemplate - Instância do JdbcTemplate para operações de banco de dados.
     */
    public AlunoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Busca um aluno pelo seu apelido.
     * Usa JdbcTemplate para executar a consulta SQL.
     * Retorna um Optional contendo o Aluno se encontrado, ou vazio se não encontrado.
     *
     * @param apelido - Apelido do aluno a ser buscado.
     * @return Optional<Aluno> - Aluno encontrado ou vazio se não encontrado.
     */
    @Override
    public Optional<Aluno> findByApelido(String apelido) {
        String sql = "SELECT id, apelido, senha FROM aluno WHERE apelido = ?";
        try {
            /** queryForObject executa a consulta SQL e mapeia o resultado para um objeto Aluno.
             * Se nenhum resultado for encontrado, lança EmptyResultDataAccessException.
             */
            Aluno aluno = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Aluno a = new Aluno();
                a.setId(rs.getLong("id"));
                a.setApelido(rs.getString("apelido"));
                a.setSenha(rs.getString("senha"));
                return a;
            }, apelido);
            return Optional.ofNullable(aluno);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Salva um aluno no banco de dados.
     * Usa JdbcTemplate para executar a inserção SQL.
     * Retorna o Aluno salvo com o ID gerado.
     *
     * @param aluno - Aluno a ser salvo.
     * @return Aluno - Aluno salvo com ID gerado.
     */
    @Override
    public Aluno salvar(Aluno aluno) {
        String sql = "INSERT INTO aluno (apelido, senha) VALUES (?, ?)";
        /** KeyHolder é usado para capturar a chave gerada (ID) após a inserção.
         * GeneratedKeyHolder é uma implementação concreta de KeyHolder.
         */
        KeyHolder keyHolder = new GeneratedKeyHolder();

        /** update executa a inserção SQL.
         * O JdbcTemplate utiliza o método .update() para executar comandos SQL que modificam dados no banco, como:
         * - INSERT
         * - UPDATE
         * - DELETE
         */
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, aluno.getApelido());
            ps.setString(2, aluno.getSenha());
            return ps;
        }, keyHolder);

        /** Aqui ele pega o ID de dentro do keyHolder e o setta no objeto aluno */
        aluno.setId(keyHolder.getKey().longValue());
        return aluno;
    }
}