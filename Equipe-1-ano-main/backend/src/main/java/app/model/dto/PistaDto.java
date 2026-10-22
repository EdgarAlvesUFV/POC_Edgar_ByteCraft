package app.model.dto;

public record PistaDto(
    Long Id,
    String tipo,
    String conteudo //Para urls, seja de imagens ou audios
    )
{}
