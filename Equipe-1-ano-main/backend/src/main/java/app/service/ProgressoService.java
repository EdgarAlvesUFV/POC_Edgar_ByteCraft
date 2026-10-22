package app.service;

import app.model.Progresso;
import app.model.dto.ProgressoCreateDto;
import app.repository.ProgressoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProgressoService {

    private final ProgressoRepository progressoRepository;

    public Long registrarOuAtualizar(ProgressoCreateDto dto) {
        Progresso existente = progressoRepository
                .buscarPorSessaoMisterio(dto.sessaoMisterioId())
                .orElse(null);

        if (existente == null) {
            Progresso novo = new Progresso();
            novo.setSessaoMisterioId(dto.sessaoMisterioId());
            novo.setPontuacaoObtida(dto.pontuacaoObtida() != null ? dto.pontuacaoObtida() : 0);
            novo.setTempoResolucaoMs(dto.tempoResolucaoMs() != null ? dto.tempoResolucaoMs() : 0L);
            novo.setResolvido(dto.resolvido() != null ? dto.resolvido() : false);
            return progressoRepository.inserir(novo);
        } else {
            existente.setPontuacaoObtida(
                    dto.pontuacaoObtida() != null ? dto.pontuacaoObtida() : existente.getPontuacaoObtida());
            existente.setTempoResolucaoMs(
                    dto.tempoResolucaoMs() != null ? dto.tempoResolucaoMs() : existente.getTempoResolucaoMs());
            if (dto.resolvido() != null) {
                existente.setResolvido(dto.resolvido());
            }
            progressoRepository.atualizar(existente);
            return existente.getId();
        }
    }
}
