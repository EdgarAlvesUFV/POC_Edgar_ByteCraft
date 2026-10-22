package app.controller;

import app.model.dto.MetricasProgressoDto;
import app.service.MetricasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/metricas")
@RequiredArgsConstructor
public class MetricaController {

    private final MetricasService metricaService;

    /**
     * 🔹 Métricas de uma sessão específica (usada no jogo)
     */
    @GetMapping("/sessao/{sessaoId}")
    public ResponseEntity<MetricasProgressoDto> getMetricasPorSessao(@PathVariable Long sessaoId) {
        MetricasProgressoDto dto = metricaService.calcularMetricaSessao(sessaoId);
        return ResponseEntity.ok(dto);
    }

    /**
     * 🔹 Métricas de uma turma (média entre todos os alunos da turma)
     */
    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<Map<String, Object>> getMetricasPorTurma(@PathVariable Long turmaId) {
        return ResponseEntity.ok(metricaService.calcularMetricaTurma(turmaId));
    }

    /**
     * 🔹 Métricas agregadas de todas as turmas de um professor
     */
    @GetMapping("/professora/{professorId}/turmas")
    public ResponseEntity<List<Map<String, Object>>> getMetricasTurmasProfessor(@PathVariable Long professorId) {
        return ResponseEntity.ok(metricaService.calcularMetricasPorProfessor(professorId));
    }

    @GetMapping("/turma/{turmaId}/graficos")
    public ResponseEntity<Map<String, Object>> getGraficosPorTurma(@PathVariable Long turmaId) {
        Map<String, Object> graficos = metricaService.gerarGraficosPorTurma(turmaId);
        return ResponseEntity.ok(graficos);
    }

}
