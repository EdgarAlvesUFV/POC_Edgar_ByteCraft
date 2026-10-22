package app.service;

import app.model.Progresso;
import app.model.Tentativa;
import app.model.PistaUso;
import app.model.dto.MetricasProgressoDto;
import app.repository.MetricaRepository;
import app.repository.ProgressoRepository;
import app.repository.PistaUsoRepository;
import app.repository.TentativaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricasService {

    private final ProgressoRepository progressoRepository;
    private final TentativaRepository tentativaRepository;
    private final PistaUsoRepository pistaUsoRepository;
    private final MetricaRepository metricaRepository;

    // === 🔹 MÉTRICAS DE UMA SESSÃO (usado no jogo) ===
    public MetricasProgressoDto calcularMetricaSessao(Long sessaoId) {
        List<Progresso> progressos = progressoRepository.buscarPorSessao(sessaoId);
        List<Long> progressoIds = progressos.stream().map(Progresso::getId).toList();

        List<Tentativa> tentativas = tentativaRepository.buscarListaProgressoID(progressoIds);
        List<PistaUso> pistas = pistaUsoRepository.buscarListaProgressoID(progressoIds);

        long totalResolvidos = progressos.stream().filter(Progresso::isResolvido).count();
        double mediaEstrelas = metricaRepository.calcularMediaEstrelas(pistas);
        double mediaTentativas = metricaRepository.calcularMediaTentativas(tentativas, progressoIds);

        return new MetricasProgressoDto(totalResolvidos, mediaEstrelas, mediaTentativas);
    }

    // === 🔹 MÉTRICAS DE UMA TURMA ===
    public Map<String, Object> calcularMetricaTurma(Long turmaId) {
        return metricaRepository.calcularMetricasTurma(turmaId);
    }

    // === 🔹 MÉTRICAS AGRUPADAS DE TODAS AS TURMAS DE UM PROFESSOR ===
    public List<Map<String, Object>> calcularMetricasPorProfessor(Long professorId) {
        return metricaRepository.calcularMetricasProfessor(professorId);
    }

    public Map<String, Object> gerarGraficosPorTurma(Long turmaId) {
        Map<String, Object> resposta = new HashMap<>();

        // 1️⃣ Agrupa por tipo de pista e número de estrelas
        List<Map<String, Object>> estrelasPorPista = metricaRepository.buscarDistribuicaoEstrelasPorTipoPista(turmaId);
        resposta.put("distribuicaoEstrelas", estrelasPorPista);

        // 2️⃣ Tempo médio de resolução por tipo de pista
        List<Map<String, Object>> tempoPorPista = metricaRepository.buscarTempoMedioPorTipoPista(turmaId);
        resposta.put("tempoPorPista", tempoPorPista);

        return resposta;
    }


}
