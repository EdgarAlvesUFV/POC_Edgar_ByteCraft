package app.controller;


import app.exception.ResourceNotFoundException;
import app.model.dto.MetricasProgressoDto;
import app.model.dto.ProgressoCreateDto;
import app.service.MetricasService;
import app.service.ProgressoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;


@RestController
@RequestMapping("/progresso")
@RequiredArgsConstructor
public class ProgressoController {

    private final MetricasService metricasService;
    private  final ProgressoService progressoService;

    @PostMapping
    public ResponseEntity<?> registrarOuAtualizar(@RequestBody ProgressoCreateDto dto) {
        Long id = progressoService.registrarOuAtualizar(dto);
        return ResponseEntity.ok(
                java.util.Map.of(
                        "status", "ok",
                        "id", id
                )
        );
    }

    /*@GetMapping(value = "/{sessaold}/metricas", produces = "application/json")
    public ResponseEntity<?> getMetricasProgresso(@PathVariable Long sessaold) {

        try {
            MetricasProgressoDto dto = metricasService.calcularMetricaSessao(sessaold);

            return ResponseEntity.ok(dto);

        } catch (ResourceNotFoundException e) {
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("status", "erro");
            errorResponse.put("codigo", 404);
            errorResponse.put("mensagem", "Recurso não encontrado.");
            errorResponse.put("detalhes", e.getMessage());


            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (app.exception.InternalServerErrorException e) {
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("status", "erro");
            errorResponse.put("codigo", 500);
            errorResponse.put("mensagem", "Ocorreu um erro interno no servidor.");
            errorResponse.put("detalhes",e.getMessage());


            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }*/
}