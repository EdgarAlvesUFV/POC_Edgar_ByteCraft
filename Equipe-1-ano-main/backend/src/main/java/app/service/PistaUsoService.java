package app.service;

import app.model.Progresso;
import app.model.dto.PistaUsoCreateDto;
import app.repository.PistaUsoRepository;
import app.repository.ProgressoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PistaUsoService {

    private final ProgressoRepository progressoRepository;
    private final PistaUsoRepository pistaUsoRepository;

    public Long registrarPistaUso(PistaUsoCreateDto dto) {
        // Garantir que o progresso existe para o sessao_misterio_id
        Progresso progresso = progressoRepository
                .buscarPorSessaoMisterio(dto.sessaoMisterioId())
                .orElseGet(() -> {
                    Progresso novo = new Progresso();
                    novo.setSessaoMisterioId(dto.sessaoMisterioId());
                    novo.setPontuacaoObtida(0);
                    novo.setTempoResolucaoMs(0L);
                    novo.setResolvido(false);
                    Long idGerado = progressoRepository.inserir(novo);
                    novo.setId(idGerado);
                    return novo;
                });

        // Inserir o uso da pista
        return pistaUsoRepository.inserirPistaUso(
                progresso.getId(),
                dto.sessaoMisterioId(),
                dto.pistaId(),
                dto.ordem(),
                dto.tipoPista()
        );
    }
}
