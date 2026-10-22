package app.repository;

import app.model.Professor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class ProfessorRepository {

    private final JdbcTemplate jdbc;

    public ProfessorRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Professor> RM = new RowMapper<>() {
        @Override
        public Professor mapRow(ResultSet rs, int rowNum) throws SQLException {
            Professor p = new Professor();
            p.setId(rs.getLong("id"));
            p.setNome(rs.getString("nome"));
            p.setEmail(rs.getString("email"));
            p.setHashSenha(rs.getString("hash_senha"));
            return p;
        }
    };

    public Optional<Professor> findByEmailAndSenhaHash(String email, String senhaHash) {
        String sql = "SELECT id, nome, email, hash_senha FROM professores WHERE email = ? AND hash_senha = ?";
        try {
            Professor professor = jdbc.queryForObject(sql, (rs, rowNum) -> {
                Professor p = new Professor();
                p.setId(rs.getLong("id"));
                p.setEmail(rs.getString("email"));
                p.setHashSenha(rs.getString("hash_senha"));
                p.setNome(rs.getString("nome"));
                return p;
            }, email, senhaHash);
            return Optional.ofNullable(professor);
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    public boolean emailExiste(String email) {
        String sql = "SELECT COUNT(*) FROM professores WHERE email = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public Professor save(Professor professor) {
        String sql = """
            INSERT INTO professores (nome, email, hash_senha)
            VALUES (?, ?, ?)
        """;
        jdbc.update(sql, professor.getNome(), professor.getEmail(), professor.getHashSenha());
        return professor;
    }

    public Optional<Professor> findByEmail(String email) {
        String sql = "SELECT * FROM professores WHERE email = ?";
        return jdbc.query(sql, RM, email).stream().findFirst();
    }

}
