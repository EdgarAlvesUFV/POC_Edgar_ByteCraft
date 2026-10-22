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

    /**
     * 🔹 Calcula médias simples da turma
     */
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


    /**
     * 🔹 Calcula métricas agregadas por turma de um professor
     */
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

    /**
     * 🔹 Calcula média de estrelas (baseada em quantidade de pistas usadas)
     */
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

    /**
     * 🔹 Calcula média de tentativas por mistério resolvido
     */
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

    /**
     * 🔹 Tempo médio de resolução por tipo de pista (para gráfico de barras)
     */
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
