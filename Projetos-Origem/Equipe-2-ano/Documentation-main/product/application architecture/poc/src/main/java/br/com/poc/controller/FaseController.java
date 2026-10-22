package br.com.poc.controller;

import br.com.poc.model.Fase;
import br.com.poc.model.PontoDeRede;
import br.com.poc.service.FaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
/**
 * @RequestMapping("/api/fases"): Define o caminho base para todos os endpoints nesta classe.
 * Todos os endpoints definidos aqui começarão com "/api/fases".
 */
@RequestMapping("/api/fases")
public class FaseController {

    private final FaseService faseService;

    public FaseController(FaseService faseService) {
        this.faseService = faseService;
    }

    /**
     * GetMapping: Mapeia este método para requisições HTTP do tipo GET.
     * Como não há um caminho específico, ele responde à URL base: "http://localhost:8080/api/fases".
     */
    @GetMapping
    public ResponseEntity<List<Fase>> getFases() {
        /**
         * Primeiro é chamado o método 'listarFases' do faseService,
        *  que retornará uma lista de objetos Fase, que será envolvido em um ResponseEntity.
        * O método .ok() define o status da resposta como 200 OK.
        * E por fim o Spring converte a lista de Fases em um array de objetos JSON.
         */
        return ResponseEntity.ok(faseService.listarFases());
    }

    @GetMapping("/3/layout")
    public ResponseEntity<List<PontoDeRede>> getLayoutFase3() {
        return ResponseEntity.ok(faseService.carregarLayoutFase3());
    }

    /**
     * @PostMapping("/3/validar"): Mapeia para requisições POST.
     */
    @PostMapping("/3/validar")
    public ResponseEntity<Boolean> validarCaminhoFase3(@RequestBody List<Long> caminho) {
        return ResponseEntity.ok(faseService.validarCaminhoFase3(caminho));
    }
}