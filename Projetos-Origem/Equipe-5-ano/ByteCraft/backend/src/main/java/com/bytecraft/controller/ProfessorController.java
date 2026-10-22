package com.bytecraft.controller;

import com.bytecraft.DTO.ProfessorDTO;
import com.bytecraft.model.Professor;
import com.bytecraft.service.ProfessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/professores")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    // 🔹 Método utilitário para normalizar texto
    private String normalizar(String texto) {
        return texto == null ? "" : texto.trim().replaceAll("\\s+", " ");
    }

    // Cadastro do professor + criação de sala automática
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody Map<String, String> payload) {
        String nome = normalizar(payload.get("nome"));
        String senha = normalizar(payload.get("senha"));
        String nomeTurma = normalizar(payload.get("nomeTurma"));

        if (nome.isEmpty() || senha.isEmpty() || nomeTurma.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Nome, senha e nome da turma são obrigatórios."));
        }

        try {
            ProfessorDTO professorDTO = professorService.cadastrarProfessor(nome, senha, nomeTurma);
            return ResponseEntity.ok(professorDTO);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // Autenticação do professor
    @PostMapping("/autenticar")
    public ResponseEntity<?> autenticar(@RequestBody Map<String, String> payload) {
        String nome = normalizar(payload.get("nome"));
        String senha = normalizar(payload.get("senha"));

        if (nome.isEmpty() || senha.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Nome e senha são obrigatórios."));
        }

        Professor professor = professorService.autenticarProfessor(nome, senha);
        if (professor != null) {
            ProfessorDTO professorDTO = ProfessorDTO.fromEntity(professor);
            return ResponseEntity.ok(professorDTO);
        } else {
            return ResponseEntity.status(401).body(Map.of("erro", "Nome de usuário ou senha inválidos."));
        }
    }

    @GetMapping("/{nomeProfessor}/sala")
    public ResponseEntity<?> getSala(@PathVariable String nomeProfessor) {
        try {
            // Chama o service para buscar a sala do professor
            var salaDTO = professorService.getSalaByNomeProfessor(nomeProfessor);

            if (salaDTO != null) {
                return ResponseEntity.ok(salaDTO);
            } else {
                return ResponseEntity.status(404)
                        .body(Map.of("erro", "Professor ou sala não encontrados."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }
}
