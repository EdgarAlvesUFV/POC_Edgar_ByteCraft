package br.com.poc.controller;

import br.com.poc.model.Aluno;
import br.com.poc.service.AlunoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @RestController: Uma anotação do Spring que combina duas funcionalidades:
 * - @Controller: Marca a classe como um controlador web.
 * - @ResponseBody: Converte automaticamente o retorno dos métodos (objetos Java) em JSON.
 */
@RestController
/**
 * @RequestMapping("/api"): Define o caminho base para todos os endpoints nesta classe.
 * Todos os endpoints definidos aqui começarão com "/api".
 */
@RequestMapping("/api")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    /**
     * @PostMapping("/login"): Define um endpoint que responde a requisições HTTP POST no caminho "/api/login".
     * @RequestBody Aluno aluno: Indica que o corpo da requisição deve ser convertido em um objeto Aluno.
     * ResponseEntity<Aluno>: Representa a resposta HTTP, incluindo o status e o corpo (objeto Aluno).
     */
    @PostMapping("/login")
    public ResponseEntity<Aluno> login(@RequestBody Aluno aluno) {
        Aluno alunoLogado = alunoService.loginOuCadastro(aluno.getApelido(), aluno.getSenha());
        return ResponseEntity.ok(alunoLogado);
    }
}
