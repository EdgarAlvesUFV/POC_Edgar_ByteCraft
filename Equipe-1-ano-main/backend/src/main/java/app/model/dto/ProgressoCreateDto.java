package app.model.dto;

public record ProgressoCreateDto(
        Long sessaoMisterioId,   // referência direta à tabela sessao_misterios
        Integer pontuacaoObtida,
        Long tempoResolucaoMs,
        Boolean resolvido
) {}
