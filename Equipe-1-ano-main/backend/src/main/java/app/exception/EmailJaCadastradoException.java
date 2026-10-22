package app.exception;

public class EmailJaCadastradoException extends RuntimeException {
    public EmailJaCadastradoException(String email) {
        super("Já existe um professor cadastrado com o email: " + email);
    }
}
