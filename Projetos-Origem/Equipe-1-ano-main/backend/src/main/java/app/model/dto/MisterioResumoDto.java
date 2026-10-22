package app.model.dto;

public record MisterioResumoDto(
        Long misterioID,
        double estrelas,
        int pontuacao,
        long tempoResolucaoMs,
        int pistasUsadas,
        String medalhaConquista
) {}
