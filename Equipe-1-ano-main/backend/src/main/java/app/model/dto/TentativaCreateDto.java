package app.model.dto;

import java.time.LocalDateTime;

public record TentativaCreateDto(
        Long sessaoId,
        Long misterioId,
        Long respostaId,      // veio do front como "respostaId"
        Boolean correta,
        Integer tempoSegundos // no front você mandou em segundos
) {}
