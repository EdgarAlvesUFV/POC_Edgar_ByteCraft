package br.com.poc.dao;


import br.com.poc.model.Fase;
import br.com.poc.model.PontoDeRede;

import java.util.List;

/**
 * Interface para operações de acesso a dados de Fase.
 * Define métodos para listar fases e carregar o layout de uma fase específica.
 *
 * @author Nana
 */
public interface FaseDAO {
    List<Fase> listarTodas();
    List<PontoDeRede> carregarLayoutFase(long faseId);
}