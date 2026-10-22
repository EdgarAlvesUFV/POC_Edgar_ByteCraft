package app.repository;

import app.model.Professor;
import app.model.Turma;
import app.model.dto.TurmaRequestDto;
import app.model.dto.TurmaResponseDto;
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
public class TurmaRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Turma> TURMA_RM = (rs, i) -> {
        Turma turma = new Turma();
        turma.setId(rs.getLong("id"));
        turma.setNome(rs.getString("nome"));
        turma.setAno(rs.getInt("ano"));
        turma.setTurno(rs.getString("turno"));

        Professor p = new Professor();
        p.setId(rs.getLong("professor_id"));
        turma.setProfessor(p);

        return turma;
    };

    private static final RowMapper<TurmaResponseDto> TURMA_RESPONSE_MAPPER = (rs, rowNum) ->
            new TurmaResponseDto(
                    rs.getLong("id"),
                    rs.getString("nome"),
                    rs.getInt("ano"),
                    rs.getString("turno")
            );

    public List<Turma> listarTurmasDoProfessor(long professorId) {
        String sql = """
            SELECT id, nome, ano, turno, professor_id
            FROM turmas
            WHERE professor_id = ?
        """;
        return jdbcTemplate.query(sql, TURMA_RM, professorId);
    }

    public Turma save(Turma turma) {
        String sql = """
            INSERT INTO turmas (nome, ano, turno, professor_id)
            VALUES (?, ?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, turma.getNome());
            ps.setInt(2, turma.getAno());
            ps.setString(3, turma.getTurno());
            ps.setLong(4, turma.getProfessor().getId()); // FK para professor
            return ps;
        }, keyHolder);

        Optional.ofNullable(keyHolder.getKey())
                .ifPresent(key -> turma.setId(key.longValue()));

        return turma;
    }

    public void deletarTurma(Long id) {
        String sql = "DELETE FROM turmas WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);

        if (rowsAffected == 0) {
            throw new IllegalArgumentException("Nenhuma turma encontrada com o id " + id);
        }
    }

    public TurmaResponseDto modificarTurma(Long id, Long professorId, TurmaRequestDto dto) {
        final String sqlUpdate = """
            UPDATE turmas
               SET nome = ?, ano = ?, turno = ?
             WHERE id = ? AND professor_id = ?
        """;

        int updated = jdbcTemplate.update(sqlUpdate,
                dto.nome(), dto.ano(), dto.turno(),
                id, professorId
        );

        if (updated == 0) {
            throw new IllegalArgumentException("Turma não encontrada ou sem permissão para alterar.");
        }

        final String sqlSelect = """
            SELECT id, nome, ano, turno, professor_id
            FROM turmas
            WHERE id = ? AND professor_id = ?
        """;

        return jdbcTemplate.queryForObject(sqlSelect, TURMA_RESPONSE_MAPPER, id, professorId);
    }

    public List<TurmaResponseDto> buscarTurmas() {
        String sql = """
        SELECT id, nome, ano, turno, professor_id
        FROM turmas
    """;
        return jdbcTemplate.query(sql, TURMA_RESPONSE_MAPPER);
    }

    public TurmaResponseDto buscarTurmaEspecifica(Long id) {
        String sql = """
        SELECT id, nome, ano, turno, professor_id
        FROM turmas
        WHERE id = ?
    """;
        return jdbcTemplate.queryForObject(sql, TURMA_RESPONSE_MAPPER, id);
    }

    public Turma buscarTurmaEntity(Long id) {
        String sql = """
        SELECT id, nome, ano, turno, professor_id
        FROM turmas
        WHERE id = ?
    """;
        return jdbcTemplate.queryForObject(sql, TURMA_RM, id);
    }
}
