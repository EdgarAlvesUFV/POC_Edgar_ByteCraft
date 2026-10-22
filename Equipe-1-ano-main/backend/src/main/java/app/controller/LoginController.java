package app.controller;

import app.model.Professor;
import app.repository.ProfessorRepository;
import app.service.JwtService;
import app.service.AutenticacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/login")
public class LoginController {

    private final AutenticacaoService autenticacaoService;
    private final ProfessorRepository professorRepository;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<?> autenticar(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            // Agora retorna um Optional<String> com o token ou vazio
            Optional<String> tokenOptional = autenticacaoService.autenticarProfessor(
                    loginRequest.getEmail(),
                    loginRequest.getSenha()
            );

            if (tokenOptional.isPresent()) {
                String token = tokenOptional.get();

                // Busca o professor para pegar o ID
                Optional<Professor> professorOpt = professorRepository.findByEmail(loginRequest.getEmail());
                if (professorOpt.isEmpty()) {
                    return ResponseEntity.status(404)
                            .body(Map.of("erro", "Professor não encontrado."));
                }

                Professor professor = professorOpt.get();

                return ResponseEntity.ok(Map.of(
                        "mensagem", "Login realizado com sucesso.",
                        "id", professor.getId(),
                        "email", professor.getEmail(),
                        "token", token
                ));
            } else {
                return ResponseEntity.status(401)
                        .body(Map.of("erro", "Email ou senha inválidos."));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("erro", "Falha inesperada no servidor: " + e.getMessage()));
        }
    }

    // =======================
    // Classe interna DTO
    // =======================
    public static class LoginRequest {
        private String email;
        private String senha;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSenha() {
            return senha;
        }

        public void setSenha(String senha) {
            this.senha = senha;
        }
    }
}
