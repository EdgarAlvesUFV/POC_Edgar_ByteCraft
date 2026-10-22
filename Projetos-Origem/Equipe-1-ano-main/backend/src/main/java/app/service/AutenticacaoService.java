package app.service;

import app.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;

@Service
public class AutenticacaoService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private JwtService jwtService;

    public Optional<String> autenticarProfessor(String email, String senha) {
        try {
            if (email == null || email.isBlank() || senha == null || senha.isBlank())
                return Optional.empty();

            String senhaHash = gerarHashSenha(senha);
            boolean credenciaisOk = professorRepository.findByEmailAndSenhaHash(email, senhaHash).isPresent();

            if (credenciaisOk) {
                String token = jwtService.gerarToken(email);
                return Optional.of(token);
            }

            return Optional.empty();

        } catch (Exception e) {
            System.err.println("Erro durante autenticação: " + e.getMessage());
            return Optional.empty();
        }
    }

    private String gerarHashSenha(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }

    public boolean emailExiste(String email) {
        try {
            return professorRepository.emailExiste(email);
        } catch (Exception e) {
            System.err.println("Erro ao verificar email: " + e.getMessage());
            return false;
        }
    }


}
