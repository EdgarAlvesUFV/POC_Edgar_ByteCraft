package app.service;

import app.model.Progresso;
import app.model.dto.TentativaCreateDto;
import app.repository.ProgressoRepository;
import app.repository.TentativaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TentativaService {

    private final ProgressoRepository progressoRepository;
    private final TentativaRepository tentativaRepository;

    /**
     * Registra uma tentativa. Se não existir Progresso para o sessao_misterio_id correspondente,
     * cria antes.
     */
    public Long registrarTentativa(TentativaCreateDto dto) {
        // 1️⃣ Buscar o ID da relação sessão-mistério
        Long sessaoMisterioId = progressoRepository.buscarSessaoMisterioId(dto.sessaoId(), dto.misterioId());
        if (sessaoMisterioId == null) {
            throw new IllegalArgumentException("Relação sessão-mistério não encontrada para os IDs fornecidos.");
        }

        // 2️⃣ Garantir que existe progresso para essa relação
        Progresso progresso = progressoRepository
                .buscarPorSessaoMisterio(sessaoMisterioId)
                .orElseGet(() -> {
                    Progresso novo = new Progresso();
                    novo.setSessaoMisterioId(sessaoMisterioId);
                    novo.setPontuacaoObtida(0);
                    novo.setTempoResolucaoMs(0L);
                    novo.setResolvido(false);
                    Long idGerado = progressoRepository.inserir(novo);
                    novo.setId(idGerado);
                    return novo;
                });

        // 3️⃣ Registra a tentativa
        long tempoMs = dto.tempoSegundos() != null ? dto.tempoSegundos() * 1000L : 0L;

        return tentativaRepository.inserirTentativa(
                progresso.getId(),          // progresso_id
                dto.misterioId(),           // continua servindo para rastrear
                dto.respostaId(),
                dto.correta() != null && dto.correta(),
                tempoMs
        );
    }
}
