package app.service;

import app.exception.EmailJaCadastradoException;
import app.model.dto.ProfessorRequestDTO;
import app.model.dto.ProfessorResponseDTO;
import app.model.Professor;
import app.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Base64;

@Service
public class ProfessorService {

    private final ProfessorRepository repository;

    public ProfessorService(ProfessorRepository repository) {
        this.repository = repository;
    }

    public ProfessorResponseDTO cadastrar(ProfessorRequestDTO dto) {
        if (repository.emailExiste(dto.email())) {
            throw new EmailJaCadastradoException(dto.email());
        }

        String hashSenha = gerarHashSenha(dto.senha());
        Professor professor = new Professor(dto.nome(), dto.email(), hashSenha);
        repository.save(professor);

        return new ProfessorResponseDTO(
                professor.getId(),
                professor.getNome(),
                professor.getEmail()
        );
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
}
