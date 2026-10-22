package br.com.poc.service;

import br.com.poc.dao.FaseDAO;
import br.com.poc.model.Fase;
import br.com.poc.model.PontoDeRede;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @Service: Uma anotação do Spring que marca esta classe como um componente da camada de serviço.
 */
@Service
public class FaseService {

    private final FaseDAO faseDAO;

    public FaseService(FaseDAO faseDAO) {
        this.faseDAO = faseDAO;
    }

    public List<Fase> listarFases() {
        return faseDAO.listarTodas();
    }

    public List<PontoDeRede> carregarLayoutFase3() {
        return faseDAO.carregarLayoutFase(3L);
    }

    public boolean validarCaminhoFase3(List<Long> caminho) {
        List<Long> caminhoCorreto = Arrays.asList(1L, 3L, 5L, 6L);
        return caminho.equals(caminhoCorreto);
    }
}
