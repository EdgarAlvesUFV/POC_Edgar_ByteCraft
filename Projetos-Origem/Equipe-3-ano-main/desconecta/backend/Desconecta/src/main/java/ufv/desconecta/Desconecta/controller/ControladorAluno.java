package ufv.desconecta.Desconecta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ufv.desconecta.Desconecta.model.EnumNomeIlha;
import ufv.desconecta.Desconecta.model.Aluno;
import ufv.desconecta.Desconecta.model.Ilha;
import ufv.desconecta.Desconecta.model.ProgressoAluno;
import ufv.desconecta.Desconecta.repository.AcessoBDAluno;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/aluno")
@CrossOrigin(origins = "*") // permitir requisições do React
public class ControladorAluno {

    private final AcessoBDAluno acessoBDAluno;

    @Autowired
    public ControladorAluno(AcessoBDAluno acessoBDAluno) {
        this.acessoBDAluno = acessoBDAluno;
    }

    public Boolean validarDadosAluno(String apelido, String senha){
        if(apelido == null || apelido.isEmpty() || senha == null || senha.isEmpty()){
            return false;
        }
        return true;
    }

    @PostMapping("/cadastro")
    public Boolean realizarCadastro(@RequestBody Aluno aluno){
        if(validarDadosAluno(aluno.getApelido(), aluno.getSenha())){
            // Verifica se o apelido já existe antes de tentar inserir
            if(acessoBDAluno.verificarApelidoExistente(aluno.getApelido())){
                return false; // Apelido já existe, retorna false
            }
            ProgressoAluno novoProgresso = new ProgressoAluno();
            novoProgresso.setPontuacaoTotalAluno(0);

            Ilha ilhaInicial = new Ilha();
            ilhaInicial.setNomeIlha(EnumNomeIlha.DADOLANDIA);
            ilhaInicial.setEstado(true);
            ilhaInicial.setFoiJogada(false);

            novoProgresso.setAluno(aluno);

            novoProgresso.setIlhas(new ArrayList<>());
            novoProgresso.getIlhas().add(ilhaInicial);
            ilhaInicial.setProgressoAluno(novoProgresso);


            return acessoBDAluno.inserirAluno(aluno);
        } else {
            return false;
        }
    }

    @PostMapping("/login")
    public Long autenticarAluno(@RequestBody Aluno loginData){
        if(validarDadosAluno(loginData.getApelido(), loginData.getSenha())){
            Aluno aluno = acessoBDAluno.buscarApelido(loginData.getApelido());
            if(aluno != null && aluno.getSenha().equals(loginData.getSenha())){
                return aluno.getPK_Aluno(); // Retorna o ID do aluno
            }
        }
        return 0L; // Retorna 0 para falha na autenticação
    }

    @GetMapping("/buscar/{apelido}")
    public Aluno buscarAluno(@PathVariable String apelido){
        return acessoBDAluno.buscarApelido(apelido);
    }

    @GetMapping("/verificar/{apelido}")
    public Boolean verificarApelidoExistente(@PathVariable String apelido){
        return acessoBDAluno.verificarApelidoExistente(apelido);
    }

    @DeleteMapping("/deletar/{apelido}")
    public Boolean deletarAluno(@PathVariable String apelido){
        return acessoBDAluno.apagarAluno(apelido);
    }


}