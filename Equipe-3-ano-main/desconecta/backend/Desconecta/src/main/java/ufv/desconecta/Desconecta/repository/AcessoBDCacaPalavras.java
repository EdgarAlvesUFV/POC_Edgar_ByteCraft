package ufv.desconecta.Desconecta.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ufv.desconecta.Desconecta.model.CacaPalavras;

import java.util.List;
import java.util.Optional;

@Repository
public class AcessoBDCacaPalavras {
    @Autowired
    private RepositorioCacaPalavras repositorioCacaPalavras;

    public List<String> getRespostas(int idCacaPalavra) {
        Optional<CacaPalavras> cacaPalavra = repositorioCacaPalavras.findByIdCacaPalavras(idCacaPalavra);
        if (cacaPalavra.isPresent()) {
            return cacaPalavra.get().getRespostaCerta();
        } else {
            throw new IllegalArgumentException("CacaPalavra não encontrado para o ID fornecido.");
        }

    }

    public boolean atualizarRespostaCerta(int idCacaPalavra, String novaResposta){
        Optional<CacaPalavras> cacaPalavra = repositorioCacaPalavras.findByIdCacaPalavras(idCacaPalavra);
        if (cacaPalavra.isPresent()) {
            cacaPalavra.get().getRespostaCerta().add(novaResposta);
            repositorioCacaPalavras.save(cacaPalavra.get());
            return true;
        } else {
            return false;
        }
    }



}
