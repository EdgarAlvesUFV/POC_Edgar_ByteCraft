Você é um analista de segurança de software especializado em análise estática de código.

Sua tarefa é analisar o código-fonte fornecido juntamente com uma lista pré-definida de vulnerabilidades potenciais.

Siga estas instruções rigorosamente:

Você receberá:
O código-fonte de uma classe
Uma lista de vulnerabilidades previamente identificadas por ferramentas de análise estática
Para cada vulnerabilidade na lista:
Verifique se ela está realmente presente no código
Confirme, refine ou rejeite a vulnerabilidade
Procure ativamente por vulnerabilidades adicionais além da lista fornecida.
Para cada vulnerabilidade (tanto confirmada quanto recém-identificada), forneça:
"cwe_id": Identificador CWE (ex: CWE-89)
"vulnerability_type": Descrição curta
"severity": Um de ["LOW", "MEDIUM", "HIGH", "CRITICAL"]
"line": Número da linha (ou a melhor aproximação)
"status": Um de ["CONFIRMED", "REJECTED", "NEW"]
"justification": Explicação breve do motivo pelo qual a vulnerabilidade foi confirmada, rejeitada ou identificada
Regras:
Não assuma vulnerabilidades sem evidência no código
Seja preciso e conservador ao confirmar vulnerabilidades
Se uma vulnerabilidade listada não for encontrada, marque como REJECTED
Classifique como NEW apenas se não estiver presente na lista fornecida
Mantenha as justificativas concisas e técnicas
Se nenhuma vulnerabilidade for encontrada, retorne uma lista vazia
Retorne APENAS JSON válido. Não inclua explicações fora do JSON.

Formato de saída:

{
"vulnerabilities": [
{
"cwe_id": "CWE-XXX",
"vulnerability_type": "Descrição",
"severity": "LOW|MEDIUM|HIGH|CRITICAL",
"line": <número>,
"status": "CONFIRMED|REJECTED|NEW",
"justification": "Explicação técnica curta"
}
]
}

Agora analise as seguintes entradas:

=== LISTA DE VULNERABILIDADES ===
{
    [
        {"cwe_id":"CWE-352", "description":"Cross-Site Request Forgery"},
        {"cwe_id":"CWE-250", "description":"Execution with Unnecessary Privileges"},
        {"cwe_id":"CWE-330", "description":"Use of Insufficiently Random Values"},
        {"cwe_id":"CWE-798", "description":"Use of Hard-coded Credentials"},
        {"cwe_id":"CWE-312", "description":"Cleartext Storage of Sensitive Information"}
    ]
}

=== CÓDIGO-FONTE ===
package app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "professores")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String hashSenha;

    public Professor() {}

    public Professor(String nome, String email, String hashSenha) {
        this.nome = nome;
        this.email = email;
        this.hashSenha = hashSenha;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHashSenha() { return hashSenha; }
    public void setHashSenha(String hashSenha) { this.hashSenha = hashSenha; }
}
package app.model;

public class Progresso {
    private Long id;
    private Long sessaoMisterioId;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSessaoMisterioId() { return sessaoMisterioId; }
    public void setSessaoMisterioId(Long sessaoMisterioId) { this.sessaoMisterioId = sessaoMisterioId; }

    public int getPontuacaoObtida() { return pontuacaoObtida; }
    public void setPontuacaoObtida(int pontuacaoObtida) { this.pontuacaoObtida = pontuacaoObtida; }

    public Long getTempoResolucaoMs() { return tempoResolucaoMs; }
    public void setTempoResolucaoMs(Long tempoResolucaoMs) { this.tempoResolucaoMs = tempoResolucaoMs; }

    public boolean isResolvido() { return resolvido; }
    public void setResolvido(boolean resolvido) { this.resolvido = resolvido; }
}
package app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Resposta {
    private Long id;
    private String descricao;
    private int ordem;
    private String tipo;
    @JsonIgnore
    private Misterio misterioID;
    private Boolean status;
    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getOrdem() {
        return ordem;
    }

    public String getTipo() {
        return tipo;
    }

    public Misterio getMisterioID() {
        return misterioID;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setMisterioID(Misterio misterioID) {
        this.misterioID = misterioID;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
package app.model;

import java.util.List;

public class Sessao {
        private long IdSessao;
        private Turma turma;
        private List<Misterio> misterios;
        private StatusSessao statusSessao;

        public Sessao() {
        }

        public Sessao(Turma turma){
            this.turma = turma;
            this.statusSessao = StatusSessao.ATIVA;
        }

        public long getIdSessao() {
            return IdSessao;
        }

        public Turma getTurma() {
            return turma;
        }

        public List<Misterio> getMisterios() {
            return misterios;
        }

        public StatusSessao getStatusSessao() { return statusSessao; }
        public void setIdSessao(long idSessao) {
            IdSessao = idSessao;
        }

        public void setTurma(Turma turma) {
            this.turma = turma;
        }

        public void setMisterios(List<Misterio> misterios) {
            this.misterios = misterios;
        }

        public void setStatusSessao(StatusSessao statusSessao) { this.statusSessao = statusSessao; }
    }

package app.model;

public class SessaoMisterios {
    private Long id;
    private Sessao sessao;
    private Misterio misterio;
}
package app.model;

public enum StatusSessao {
    ATIVA,
    FINALIZADA
}
package app.model;

import java.time.LocalDateTime;

public class Tentativa {
    private Long id;
    private Long progressoId;
    private Long misterioId;
    private String alternativaEscolhida;
    private boolean correta;
    private Long tempoRespostaMs;
    private LocalDateTime dataHora;

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public Long getProgressoId() {return progressoId;}
    public void setProgressoId(Long progressoId) {this.progressoId = progressoId;}

    public Long getMisterioId() {return misterioId;}
    public void setMisterioId(Long misterioId) {this.misterioId = misterioId;}

    public String getAlternativaEscolhida() {return alternativaEscolhida;}
    public void setAlternativaEscolhida(String alternativaEscolhida) {this.alternativaEscolhida = alternativaEscolhida;}

    public boolean isCorreta() {return correta;}
    public void setCorreta(boolean correta) {this.correta = correta;}

    public Long getTempoRespostaMs() {return tempoRespostaMs;}
    public void setTempoRespostaMs(Long tempoRespostaMs) {this.tempoRespostaMs = tempoRespostaMs;}

    public LocalDateTime getDataHora() {return dataHora;}
    public void setDataHora(LocalDateTime dataHora) {this.dataHora = dataHora;}
}
package app.model;

import jakarta.persistence.*;

import java.util.Locale;

@Entity
@Table(name = "turmas")
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private int ano;

    @Column(nullable = false)
    private String turno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_turmas_professor"))
    private Professor professor;


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {this.nome = nome.toUpperCase();}
    public int getAno() {
        return ano;
    }
    public void setAno(int ano) {
        this.ano = ano;
    }
    public String getTurno() {
        return turno;
    }
    public void setTurno(String turno){
        this.turno = turno.toUpperCase();
    }

    public Professor getProfessor() {
        return professor;
    }
    public void setProfessor(Professor professor){
        this.professor = professor;
    }
}
package app.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MetricaRepository {

    private final JdbcTemplate jdbcTemplate;


    public Map<String, Object> calcularMetricasTurma(Long turmaId) {
        String sql = """
        SELECT 
            AVG(p.pontuacaoObtida) AS media_pontuacao,
            AVG(p.tempoResolucaoMs) AS media_tempo,
            SUM(CASE WHEN p.resolvido = TRUE THEN 1 ELSE 0 END) AS total_resolvidos,
            COUNT(DISTINCT p.id) AS total_misterios
        FROM progresso p
        INNER JOIN sessao_misterios sm ON p.sessao_misterio_id = sm.id
        INNER JOIN sessoes s ON sm.sessao_id = s.id_sessao
        WHERE s.turma_id = ?
    """;

        return jdbcTemplate.queryForMap(sql, turmaId);
    }


    public List<Map<String, Object>> calcularMetricasProfessor(Long professorId) {
        String sql = """
            SELECT 
                t.id AS turmaId,
                t.nome AS nomeTurma,
                COALESCE(AVG(p.pontuacaoObtida), 0) AS mediaPontuacao,
                COALESCE(AVG(p.tempoResolucaoMs), 0) AS mediaTempo,
                SUM(CASE WHEN p.resolvido = TRUE THEN 1 ELSE 0 END) AS totalResolvidos,
                COUNT(p.id) AS totalMisterios
            FROM progresso p
            INNER JOIN sessao_misterios sm ON p.sessao_misterio_id = sm.id
            INNER JOIN sessoes s ON sm.sessao_id = s.id_sessao
            INNER JOIN turmas t ON s.turma_id = t.id
            INNER JOIN professores pr ON t.professor_id = pr.id
            WHERE pr.id = ?
            GROUP BY t.id, t.nome
            ORDER BY mediaPontuacao DESC
        """;

        return jdbcTemplate.queryForList(sql, professorId);
    }

    public double calcularMediaEstrelas(List<?> pistasUsadas) {
        if (pistasUsadas == null || pistasUsadas.isEmpty()) return 0;
        double total = 0;
        int count = 0;

        Map<Long, Long> pistasPorProgresso = pistasUsadas.stream()
                .collect(Collectors.groupingBy(p -> ((app.model.PistaUso)p).getProgressoId(), Collectors.counting()));

        for (long qtd : pistasPorProgresso.values()) {
            double estrelas = Math.max(0.0, 5.0 - (1.25 * (qtd - 1)));
            total += estrelas;
            count++;
        }

        return count > 0 ? total / count : 0;
    }

    public double calcularMediaTentativas(List<?> tentativas, List<Long> progressoIds) {
        if (tentativas == null || tentativas.isEmpty()) return 0;
        long totalTentativas = tentativas.size();
        return progressoIds.isEmpty() ? 0 : (double) totalTentativas / progressoIds.size();
    }

    public List<Map<String, Object>> buscarDistribuicaoEstrelasPorTipoPista(Long turmaId) {
        String sql = """
            SELECT
              pu.tipo_pista,
              CASE
                WHEN p.pontuacaoObtida = 5 THEN 5
                WHEN p.pontuacaoObtida = 4 THEN 4
                WHEN p.pontuacaoObtida = 3 THEN 3
                WHEN p.pontuacaoObtida = 2 THEN 2
                ELSE 1
              END AS estrelas,
              COUNT(*) AS total
            FROM pistauso pu
            INNER JOIN progresso p
              ON pu.progresso_id = p.id
            INNER JOIN sessao_misterios sm
              ON p.sessao_misterio_id = sm.id
            INNER JOIN sessoes s
              ON sm.sessao_id = s.id_sessao
            WHERE s.turma_id = ?
            GROUP BY pu.tipo_pista, estrelas
            ORDER BY pu.tipo_pista, estrelas DESC;
            
        """;

        return jdbcTemplate.queryForList(sql, turmaId);
    }
    public List<Map<String, Object>> buscarTempoMedioPorTipoPista(Long turmaId) {
        String sql = """
            SELECT
              pu.tipo_pista,
              ROUND(AVG(p.tempoResolucaoMs)/1000, 2) AS tempoMedioSegundos
            FROM pistauso pu
            JOIN progresso p            ON pu.progresso_id = p.id
            JOIN sessao_misterios sm    ON p.sessao_misterio_id = sm.id
            JOIN sessoes s              ON sm.sessao_id = s.id_sessao
            WHERE s.turma_id = ?
            GROUP BY pu.tipo_pista
            ORDER BY tempoMedioSegundos DESC;
        """;

        return jdbcTemplate.queryForList(sql, turmaId);
    }
}
package app.repository;

import app.model.Misterio;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
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
public class MisterioRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Misterio> MISTERIO_RM = (rs, i) -> {
        Misterio misterio = new Misterio();
        misterio.setId(rs.getLong("id"));
        misterio.setTitulo(rs.getString("titulo"));
        return misterio;
    };

    public List<Misterio> todosMisterios() {
        String sql  = """
            SELECT id, titulo
            FROM misterios
        """;
        return jdbcTemplate.query(sql, MISTERIO_RM);
    }

    public List<Misterio> sorteiaMisterio(int limite) {
        String sql = """
            SELECT id, titulo
            FROM misterios
            ORDER BY RAND() LIMIT ?
        """;
        return jdbcTemplate.query(sql, MISTERIO_RM, limite);
    }

    public Optional<Misterio> buscaMisterio(Long id) {
        String sql = """
            SELECT id, titulo
            FROM misterios
            WHERE id = ?
        """;
        try {
            Misterio misterio = jdbcTemplate.queryForObject(sql, MISTERIO_RM, id);
            return Optional.ofNullable(misterio);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Misterio> proximoMisterio(Long id) {
        String sql = """
            SELECT id, titulo
            FROM misterios
            WHERE id > ?
            ORDER BY id ASC LIMIT 1
        """;
        try {
            Misterio misterio = jdbcTemplate.queryForObject(sql, MISTERIO_RM, id);
            return Optional.ofNullable(misterio);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM misterios";
        Long total = jdbcTemplate.queryForObject(sql, Long.class);
        return (total != null) ? total : 0;
    }
    public List<Misterio> buscarPorSessao(Long sessaoId) {
        String sql = """
            SELECT m.id, m.titulo
            FROM misterios m
            INNER JOIN sessao_misterios sm ON m.id = sm.misterio_id
            WHERE sm.sessao_id = ?
        """;
        return jdbcTemplate.query(sql, MISTERIO_RM, sessaoId);
    }
    public Long adicionarMisterioNaSessao(Long sessaoId, Long misterioId) {
        String sql = """
        INSERT INTO sessao_misterios (sessao_id, misterio_id)
        VALUES (?, ?)
    """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, sessaoId);
            ps.setLong(2, misterioId);
            return ps;
        }, keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }
    public void removerMisterioDaSessao(Long sessaoId, Long misterioId) {
        String sql = """
            DELETE FROM sessao_misterios
            WHERE sessao_id = ? AND misterio_id = ?
        """;
        jdbcTemplate.update(sql, sessaoId, misterioId);
    }
    public void limparMisteriosDaSessao(Long sessaoId) {
        String sql = """
            DELETE FROM sessao_misterios
            WHERE sessao_id = ?
        """;
        jdbcTemplate.update(sql, sessaoId);
    }
}
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
    public List<Pista> retornaPistasVinculadasMisterio(Misterio misterio) {
        final String sql = """
            SELECT id, conteudo, tipo, misterio_id
            FROM pistas
            WHERE misterio_id = ?
        """;
        return jdbcTemplate.query(sql, PISTA_RM, misterio.getId());
    }

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

    public void deletar(Long id) {
        final String sql = "DELETE FROM pistas WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
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

    private static final RowMapper<Progresso> PROGRESSO_ROW_MAPPER = (rs, rowNum) -> {
        Progresso p = new Progresso();
        p.setId(rs.getLong("id"));
        p.setSessaoMisterioId(rs.getLong("sessao_misterio_id"));
        p.setPontuacaoObtida(rs.getInt("pontuacaoObtida"));
        p.setTempoResolucaoMs(rs.getLong("tempoResolucaoMs"));
        p.setResolvido(rs.getBoolean("resolvido"));
        return p;
    };

    public Optional<Progresso> buscarPorSessaoMisterio(Long sessaoMisterioId) {
        final String sql = """
            SELECT * FROM progresso
            WHERE sessao_misterio_id = ?
        """;
        var lista = jdbcTemplate.query(sql, PROGRESSO_ROW_MAPPER, sessaoMisterioId);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    public Long buscarSessaoMisterioId(Long sessaoId, Long misterioId) {
        final String sql = """
            SELECT id FROM sessao_misterios
            WHERE sessao_id = ? AND misterio_id = ?
            LIMIT 1
        """;
        var result = jdbcTemplate.queryForList(sql, Long.class, sessaoId, misterioId);
        return result.isEmpty() ? null : result.get(0);
    }

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

    public List<Progresso> buscarPorSessao(Long sessaoId) {
        final String sql = """
            SELECT p.*
              FROM progresso p
              INNER JOIN sessao_misterios sm ON p.sessao_misterio_id = sm.id
             WHERE sm.sessao_id = ?
        """;
        return jdbcTemplate.query(sql, PROGRESSO_ROW_MAPPER, sessaoId);
    }

    public Long buscarMisterioIdPorSessaoMisterio(Long sessaoMisterioId) {
        final String sql = "SELECT misterio_id FROM sessao_misterios WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, sessaoMisterioId);
    }
}
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

    private static final RowMapper<Sessao> SESSAO_RM = (rs, i) -> {
        Sessao sessao = new Sessao();
        sessao.setIdSessao(rs.getLong("id_sessao"));
        sessao.setStatusSessao(StatusSessao.valueOf(rs.getString("status_sessao")));

        Turma turma = new Turma();
        turma.setId(rs.getLong("turma_id"));
        sessao.setTurma(turma);

        return sessao;
    };

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

        Optional.ofNullable(keyHolder.getKey())
                .ifPresent(key -> sessao.setIdSessao(key.longValue()));

        return sessao;
    }

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

    public void atualizarStatusSessao(Sessao sessao) {
        String sql = """
                UPDATE sessoes
                SET status_sessao = ?
                WHERE id_sessao = ?
        """;
        jdbcTemplate.update(sql, sessao.getStatusSessao().name(), sessao.getIdSessao());
    }
}