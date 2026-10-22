package app.model.dto;

import java.util.List;

public record MisterioResponseDto(
        Long id,
        String enunciado,
        List<String> pistas,
        List<PistaDto> alternativas
) {}
