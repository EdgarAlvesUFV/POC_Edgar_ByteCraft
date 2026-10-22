package ufv.desconecta.Desconecta.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class AcessoBDConecta {

    @Autowired
    private RepositorioConecta repositorioConecta;

    public List<String> getRespostas(int PK_Conecta) {
        return repositorioConecta.findByIdConecta(PK_Conecta)
                .map(c -> List.of(
                        c.getRespostaCerta1(),
                        c.getRespostaCerta2(),
                        c.getRespostaCerta3(),
                        c.getRespostaCerta4()))
                .orElse(Collections.emptyList());
    }

    public Boolean atualizarRespostaCerta(int PK_Conecta, String novaResposta, int idResposta) {
       if(idResposta < 1 || idResposta > 4) {
           return false; // ID inválido
       }
         return repositorioConecta.findById((long) PK_Conecta).map(conecta -> {
              switch (idResposta) {
                case 1 -> conecta.setRespostaCerta1(novaResposta);
                case 2 -> conecta.setRespostaCerta2(novaResposta);
                case 3 -> conecta.setRespostaCerta3(novaResposta);
                case 4 -> conecta.setRespostaCerta4(novaResposta);
              }
              repositorioConecta.save(conecta);
              return true;
         }).orElse(false); // Retorna false se o conecta não foi encontrado
    }
}