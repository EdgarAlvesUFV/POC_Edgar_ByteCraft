package app.model.dto;

public record PistaUsoCreateDto(
        Long sessaoMisterioId,
        Long pistaId,
        Integer ordem,
        String tipoPista
) {}
