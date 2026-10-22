package app.service;


import app.model.Misterio;
import app.model.Pista;
import app.model.Resposta;

import app.model.dto.AvaliacaoRequestDto;
import app.model.dto.MisterioResponseDto;
import app.model.dto.PistaDto;
import app.repository.MisterioRepository;
import app.repository.PistaRepository;
import app.repository.RespostaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MisterioService {

    private final MisterioRepository misterioRepository;
    private final PistaRepository pistaRepository;
    private final RespostaRepository respostaRepository;

    private final Random random = new Random();

    /**
     * Sorteia uma lista de mistérios aleatórios para a sessão.
     */
    public List<Misterio> sortearMisterios(int qtd) {
        List<Misterio> sorteados = misterioRepository.sorteiaMisterio(qtd);
        if (sorteados.isEmpty()) {
            throw new IllegalArgumentException("Nenhum mistério disponível para a sessão " );
        }
        return sorteados;
    }

    /**
     * Sorteia pistas aleatórias de um mistério.
     */
    public List<Pista> sortearPistas(Misterio misterio, int qtd) {
        List<Pista> pistas = pistaRepository.retornaPistasVinculadasMisterio(misterio);
        if (pistas.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma pista encontrada para o mistério: " + misterio);
        }
        Collections.shuffle(pistas, random);
        return pistas.stream().limit(qtd).collect(Collectors.toList());
    }

    /**
     * Monta as alternativas (1 correta + 3 incorretas).
     */
    public List<Resposta> montarAlternativas(Long misterioId) {
        Misterio misterio = misterioRepository.buscaMisterio(misterioId)
                .orElseThrow(() -> new IllegalArgumentException("Mistério não encontrado: " + misterioId));

        Resposta correta = respostaRepository.buscaCorretaMisterio(misterioId);
        List<Resposta> incorretas = respostaRepository.buscaIncorretas( 3);

        if (incorretas.size() < 3) {
            throw new IllegalArgumentException("Não há alternativas incorretas suficientes.");
        }

        incorretas.add(correta);
        return embaralharAlternativas(incorretas);
    }

    /**
     * Embaralha a ordem das alternativas.
     */
    public List<Resposta> embaralharAlternativas(List<Resposta> respostas) {
        Collections.shuffle(respostas, random);
        return respostas;
    }

    /**
     * Valida se a resposta é correta para o mistério.
     */
    public boolean validarResposta(Long misterioId, @Valid AvaliacaoRequestDto respostaId) {
        Misterio misterio = misterioRepository.buscaMisterio(misterioId)
                .orElseThrow(() -> new NoSuchElementException("Mistério não encontrado: " + misterioId));

        Resposta correta = respostaRepository.buscaCorretaMisterio(misterioId);

        /*if (Boolean.TRUE.equals(misterio.getStatus())) {
            throw new IllegalStateException("Este mistério já foi resolvido.");
        }*/

        boolean acertou = correta.getId().equals(respostaId);



        return acertou;
    }

    public MisterioResponseDto listarMisterioSessao(Long sessaoId) {
        List<Misterio> misterio = misterioRepository.buscarPorSessao(sessaoId);
        Misterio misterio1 = misterio.get(0);
        List<PistaDto>  pistas = new ArrayList<>();

        return new MisterioResponseDto(misterio1.getId(),
                misterio1.getTitulo(),
                misterio1.getPistas()
                        .stream()
                        .map(Pista::getConteudo)
                        .toList(),
                pistas
                );
    }
}
