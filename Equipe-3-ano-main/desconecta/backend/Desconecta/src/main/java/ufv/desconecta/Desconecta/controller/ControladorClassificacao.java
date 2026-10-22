package ufv.desconecta.Desconecta.controller;

import org.springframework.web.bind.annotation.*;
import ufv.desconecta.Desconecta.dto.ClassificacaoDTO;
import ufv.desconecta.Desconecta.model.ProgressoAluno;
import ufv.desconecta.Desconecta.repository.AcessoBDProgressoAluno;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classificacao")
@CrossOrigin(origins = "*")
public class ControladorClassificacao {

  private AcessoBDProgressoAluno acessoBDProgressoAluno;

  public ControladorClassificacao(AcessoBDProgressoAluno acessoBDProgressoAluno) {
    this.acessoBDProgressoAluno = acessoBDProgressoAluno;
  }

  @GetMapping("/classificacaoGeral")
  public List<ClassificacaoDTO> buscarClassificacaoGeral() {
    List<ClassificacaoDTO> listaClassificacaoGeral = acessoBDProgressoAluno.obterClassificacaoGeralOrdenada();
    for (ClassificacaoDTO progresso : listaClassificacaoGeral) {
      System.out.println("Nome: " + progresso.getNomeAluno() +
          " Pontuação: " + progresso.getPontuacaoTotalAluno());
    }
    return listaClassificacaoGeral;
  }

  @GetMapping("/classificacaoAluno")
  public Map<String, Object> buscarClassificacaoAluno(@RequestParam String apelidoAluno) {
    List<ClassificacaoDTO> listaClassificacaoGeral = acessoBDProgressoAluno.obterClassificacaoGeralOrdenada();

    Map<String, Object> resultado = new HashMap<>();

    // Procurar o aluno na lista ordenada
    for (int i = 0; i < listaClassificacaoGeral.size(); i++) {
      ClassificacaoDTO classificacao = listaClassificacaoGeral.get(i);
      if (classificacao.getNomeAluno().equalsIgnoreCase(apelidoAluno)) {
        resultado.put("posicao", i + 1);
        resultado.put("nomeAluno", classificacao.getNomeAluno());
        resultado.put("pontuacaoTotalAluno", classificacao.getPontuacaoTotalAluno());

        System.out.println("Classificação do aluno '" + apelidoAluno + "': " +
            "Posição: " + (i + 1) + ", Pontuação: " + classificacao.getPontuacaoTotalAluno());

        return resultado;
      }
    }

    // Se não encontrar o aluno
    System.err.println("Aluno '" + apelidoAluno + "' não encontrado no ranking.");
    resultado.put("erro", "Aluno não encontrado");
    return resultado;
  }
}
