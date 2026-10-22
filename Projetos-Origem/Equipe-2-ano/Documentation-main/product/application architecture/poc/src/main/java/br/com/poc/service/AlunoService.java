package br.com.poc.service;

import br.com.poc.dao.AlunoDAO;
import br.com.poc.model.Aluno;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @Service: Uma anotação do Spring que marca esta classe como um componente da camada de serviço.
 */
@Service
public class AlunoService {

    private final AlunoDAO alunoDAO;
    private final PasswordEncoder passwordEncoder;

    public AlunoService(AlunoDAO alunoDAO, PasswordEncoder passwordEncoder) {
        this.alunoDAO = alunoDAO;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Método para login ou cadastro de um aluno.
     * Se o apelido já existir, retorna o aluno existente.
     * Se não existir, cria um novo aluno com o apelido e senha fornecidos.
     *
     * @param apelido O apelido do aluno.
     * @param senha   A senha do aluno.
     * @return O aluno existente ou o novo aluno criado.
     */
    public Aluno loginOuCadastro(String apelido, String senha) {
        Optional<Aluno> alunoExistente = alunoDAO.findByApelido(apelido);

        /**
         * Se o aluno com o apelido fornecido já existir, retorna o aluno existente.
         */
        if (alunoExistente.isPresent()) {
            Aluno alunoDoBanco = alunoExistente.get();
            if (passwordEncoder.matches(senha, alunoDoBanco.getSenha())) {
                return alunoDoBanco;
            } else {
                throw new RuntimeException("Senha inválida");
            }
        } else {
            /**
             * Se o aluno não existir, cria um novo aluno com o apelido e senha fornecidos.
             */
            Aluno novoAluno = new Aluno();
            novoAluno.setApelido(apelido);
            novoAluno.setSenha(passwordEncoder.encode(senha));
            return alunoDAO.salvar(novoAluno);
        }
    }
}
