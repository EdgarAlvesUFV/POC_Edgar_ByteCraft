package app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção para ser lançada quando ocorre um erro inesperado no servidor.
 * Mapeia para o status HTTP 500 Internal Server Error.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends RuntimeException {

    public InternalServerErrorException(String message) {
        super(message);
    }

    /**
     * Construtor crucial para erros 500.
     * Permite que a exceção original (ex: falha no banco) seja encapsulada.
     * @param message A mensagem de alto nível para o erro.
     * @param cause A exceção original (ex: DataAccessException).
     */
    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}