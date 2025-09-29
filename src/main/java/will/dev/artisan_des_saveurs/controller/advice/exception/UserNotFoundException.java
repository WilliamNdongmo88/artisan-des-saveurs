package will.dev.artisan_des_saveurs.controller.advice.exception;

/**
 * Exception levée lorsqu'un utilisateur n'est pas trouvé
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
