package app.model.dto;

import java.util.List;

public record SessaoMisterioResponseDTO(
        Long idSessao,
        Long turmaId,
        List<MisterioSessaoDTO> misterios
) {}
