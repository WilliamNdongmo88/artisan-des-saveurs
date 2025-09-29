package will.dev.artisan_des_saveurs.controller.advice.exception;

/**
 * Exception levée lorsque les credentials fournis sont invalides
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
