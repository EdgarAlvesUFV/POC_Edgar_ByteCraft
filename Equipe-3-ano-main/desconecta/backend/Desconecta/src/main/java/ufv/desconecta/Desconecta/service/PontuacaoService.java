package ufv.desconecta.Desconecta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ufv.desconecta.Desconecta.model.Desafio;
import ufv.desconecta.Desconecta.repository.AcessoBDDesafio;
import ufv.desconecta.Desconecta.repository.RepositorioDesafio;

import java.util.Optional;

@Service
public class PontuacaoService {

    @Autowired
    private AcessoBDDesafio acessoBDDesafio;

    @Autowired
    private RepositorioDesafio repositorioDesafio;


    public int calcularPontuacao(int tempo, int numErros) {
        // Lógica de cálculo de pontuação
        // Exemplo: 1000 - (tempo * 2) - (numErros * 50)

        int pontuacaoBase = 1000;
        int penalidadeTempo = tempo * 2;
        int penalidadeErros = numErros * 50;
        int pontuacaoFinal = pontuacaoBase - penalidadeTempo - penalidadeErros;
        return Math.max(pontuacaoFinal, 0); // Garante que a pontuação não seja negativa
    }

    public boolean salvarPontuacaoDesafio(int pontuacao, Desafio desafio) {
        Optional<Desafio> desafioOptional = repositorioDesafio.findById(desafio.getId());
        if(desafioOptional.isPresent()){
            Desafio desafioExistente = desafioOptional.get();
            desafioExistente.setPontuacaoDesafio(pontuacao);
            repositorioDesafio.save(desafioExistente);
            return true;
        }
        return false;
    }
}