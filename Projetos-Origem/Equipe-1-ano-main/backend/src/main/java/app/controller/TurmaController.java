package app.controller;

import app.model.Professor;
import app.model.Turma;
import app.model.dto.TurmaRequestDto;
import app.model.dto.TurmaResponseDto;
import app.service.TurmaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/turma")
public class TurmaController {

    private final TurmaService turmaService;

    @PostMapping
    public ResponseEntity<?> cadastrarTurma(@AuthenticationPrincipal Professor professorAutenticado,@RequestBody @Valid TurmaRequestDto turmaRequestDto) {
        if (professorAutenticado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Turma turmaCriada = turmaService.cadastrarTurma(turmaRequestDto, professorAutenticado);
            return ResponseEntity.status(HttpStatus.CREATED).body(turmaCriada);
        } catch (IllegalArgumentException e) {
            // Retorna 400 Bad Request com a mensagem de erro do serviço
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/professor/turmas")
    public ResponseEntity<List<TurmaResponseDto>> listarTurmasDoProfessor(@AuthenticationPrincipal Professor professorAutenticado) {
        if (professorAutenticado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<TurmaResponseDto> listaTurma = turmaService.listarTurmas(professorAutenticado);
        return ResponseEntity.ok(listaTurma);
    }



    @GetMapping
    public ResponseEntity<List<TurmaResponseDto>> listarTurmas() {
        List<TurmaResponseDto> turmas = turmaService.listarTodasTurmas();
        return ResponseEntity.ok(turmas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaResponseDto> buscarTurma(@PathVariable Long id) {
        TurmaResponseDto turma = turmaService.buscarTurma(id);
        return ResponseEntity.ok(turma);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarTurma(@AuthenticationPrincipal Professor professorAutenticado, @PathVariable Long id) {
        if (professorAutenticado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            turmaService.deletarTurma(id);
            return ResponseEntity.noContent().build(); // 204
        } catch (NoSuchElementException e) {
            // Turma não existe ou não pertence ao professor
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            // Há vínculos que impedem a exclusão (FK)
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Não é possível excluir a turma: existem registros vinculados.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificarTurma(
            @AuthenticationPrincipal Professor professorAutenticado,
            @PathVariable Long id,
            @RequestBody @Valid TurmaRequestDto turmaRequestDto) {

        if (professorAutenticado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            TurmaResponseDto turma = turmaService.modificarTurma(id, turmaRequestDto, professorAutenticado);
            return ResponseEntity.ok(turma);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}