package app.model.dto;

public record AvaliacaoResponseDto(
    String status,
    String mensagem,
    Boolean isResolvido,
    PistaDto proximaPista //Caso a resposta seje correta este campo será nulo
    )
{}