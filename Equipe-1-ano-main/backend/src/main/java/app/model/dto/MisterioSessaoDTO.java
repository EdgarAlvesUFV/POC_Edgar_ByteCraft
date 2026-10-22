package app.model.dto;

import app.model.Misterio;
import app.model.Pista;
import app.model.Resposta;
import java.util.List;

public record MisterioSessaoDTO(
        Long idMisterio,
        String titulo,
        Long sessaoMisterioId,
        List<Pista> pistas,
        List<Resposta> respostas
) {
    public static MisterioSessaoDTO from(Misterio misterio, Long sessaoMisterioId) {
        return new MisterioSessaoDTO(
                misterio.getId(),
                misterio.getTitulo(),
                sessaoMisterioId,
                misterio.getPistas(),
                misterio.getRespostas()
        );
    }
}
