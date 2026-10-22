package app.controller;

import app.model.Misterio;
import app.model.Pista;
import app.model.Resposta;
import app.model.dto.AvaliacaoResponseDto;
import app.model.dto.PistaDto;
import app.model.dto.MisterioResponseDto;
import app.model.dto.AvaliacaoRequestDto;
import app.service.MisterioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/misterio")

public class MisterioController {

    private final MisterioService misterioService;


    @GetMapping("/{id}/misterio")
    public ResponseEntity<MisterioResponseDto> listarMisterioSessao(@PathVariable("id") Long sessaoId) {
        try {
            MisterioResponseDto misterio = misterioService.listarMisterioSessao(sessaoId);
            return ResponseEntity.ok(misterio);
        } catch (NoSuchElementException e) {
            // Caso a sessão ou o mistério não seja encontrado
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/{id}/resposta")
    public ResponseEntity<?> avaliarResposta(
            @PathVariable("id") Long sessaoId,
            @RequestBody @Valid AvaliacaoRequestDto avaliacaoDto) {
        try {
            // O serviço retorna o resultado da avaliação, como o progresso atualizado.
            // O tipo de retorno pode ser ajustado (ex: ProgressoDto, Boolean, etc.).
            var resultado = misterioService.validarResposta(sessaoId, avaliacaoDto);
            return ResponseEntity.ok(resultado);
        } catch (NoSuchElementException e) {
            // Caso a sessão, mistério ou resposta não existam.
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            // Caso a requisição seja inválida (ex: resposta já enviada).
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}