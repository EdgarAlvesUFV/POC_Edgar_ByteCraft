package app.model.dto;

import app.model.Sessao;
import java.time.LocalDateTime;

/**
 * DTO para formatar a resposta da criação de uma sessão
 */
public record SessaoResponseDTO(
        Long sessaoId,
        Long turmaId,
        String status,
        LocalDateTime dataInicio
) {
    public SessaoResponseDTO(Sessao sessao) {
        this(
                sessao.getIdSessao(),
                sessao.getTurma().getId(),
                sessao.getStatusSessao().name(),
                LocalDateTime.now()
        );
    }
}
