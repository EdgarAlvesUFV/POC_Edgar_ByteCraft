package ufv.desconecta.Desconecta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ufv.desconecta.Desconecta.model.Aluno;
import ufv.desconecta.Desconecta.model.Desafio;
import ufv.desconecta.Desconecta.model.Ilha;
import ufv.desconecta.Desconecta.repository.AcessoBDAluno;
import ufv.desconecta.Desconecta.repository.AcessoBDDesafio;
import ufv.desconecta.Desconecta.repository.AcessoBDProgressoAluno;

import ufv.desconecta.Desconecta.model.ProgressoAluno;

import java.util.List;

@RestController
@RequestMapping("/api/progresso-aluno")
@CrossOrigin(origins = "*")
public class ControladorProgressoAluno {

  private final AcessoBDProgressoAluno acessoBDProgressoAluno;

  private final AcessoBDAluno acessoBDAluno;

  private final AcessoBDDesafio acessoBDDesafio;

  @Autowired
  public ControladorProgressoAluno(AcessoBDProgressoAluno acessoBDProgressoAluno, AcessoBDAluno acessoBDAluno,
      AcessoBDDesafio acessoBDDesafio) {
    this.acessoBDDesafio = acessoBDDesafio;
    this.acessoBDProgressoAluno = acessoBDProgressoAluno;
    this.acessoBDAluno = acessoBDAluno;
  }

  @GetMapping("/{idAluno}")
  public ProgressoAluno recuperarProgressoAluno(@PathVariable int idAluno) {
    return acessoBDProgressoAluno.getProgressoAluno(idAluno);
  }

  @GetMapping("/id/{idAluno}")
  public Long recuperarIdProgressoAluno(@PathVariable int idAluno) {
    ProgressoAluno progresso = acessoBDProgressoAluno.getProgressoAluno(idAluno);
    return progresso != null ? progresso.getPK_ProgressoAluno() : null;
  }

  @PostMapping("/calcularPontuacaoTotal")
  public int calcularPontuacaoTotal(@RequestParam String apelidoAluno) {
    System.out.println(apelidoAluno);

    Aluno aluno = acessoBDAluno.buscarApelido(apelidoAluno);
    if (aluno == null) {
      System.err.println("Erro ao calcular pontuação: Aluno '" + apelidoAluno + "' não encontrado.");
      return 0;
    }

    ProgressoAluno progresso = acessoBDProgressoAluno.getProgressoPeloAlunoId(aluno.getPK_Aluno());
    if (progresso == null) {
      System.err.println("Erro ao calcular pontuação: Progresso não encontrado para o aluno: " + apelidoAluno);
      return -1;
    }

    List<Desafio> desafiosDoAluno = acessoBDDesafio.getDesafiosDoAluno(aluno.getPK_Aluno());

    int pontuacaoTotal = desafiosDoAluno.stream()
        .filter(Desafio::isConcluido)
        .mapToInt(Desafio::getPontuacaoDesafio)
        .sum();

    progresso.setPontuacaoTotalAluno(pontuacaoTotal);
    acessoBDProgressoAluno.salvarProgressoAluno(progresso);

    System.out.println("Nova pontuação total para '" + apelidoAluno + "': " + pontuacaoTotal);

    return 1;
  }

}
