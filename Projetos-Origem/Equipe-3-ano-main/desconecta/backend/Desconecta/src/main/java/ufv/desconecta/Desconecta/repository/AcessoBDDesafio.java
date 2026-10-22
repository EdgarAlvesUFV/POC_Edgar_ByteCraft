package ufv.desconecta.Desconecta.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ufv.desconecta.Desconecta.model.Desafio;

import java.util.List;
import java.util.Optional;

@Repository
public class AcessoBDDesafio  {

    @Autowired
    private RepositorioDesafio repositorioDesafio;


    public boolean alterarEstadoDesafio(Desafio desafio) {
        Optional<Desafio> desafioOptional = repositorioDesafio.findById(desafio.getId());
        if (desafioOptional.isPresent()) {
            Desafio desafioExistente = desafioOptional.get();
            desafioExistente.setConcluido(desafio.isConcluido());
            repositorioDesafio.save(desafioExistente);
            return true;
        }
        return false;
    }
    public Optional<Desafio> findDesafioByIDs(Long idAluno, int idIlha, int idDesafio) {
        return repositorioDesafio.findDesafioByIDs(idAluno, idIlha, idDesafio);
    }
    public List<Desafio> getDesafiosDoAluno(long alunoId) {
        return repositorioDesafio.findDesafiosByAlunoId(alunoId);
    }

    public Desafio getDesafioByIlhaId(int ilhaId) {
        return  repositorioDesafio.findDesafiosByIlhaId(ilhaId);
    }
}