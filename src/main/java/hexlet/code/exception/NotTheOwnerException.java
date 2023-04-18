package hexlet.code.exception;

public class NotTheOwnerException extends RuntimeException {

    public NotTheOwnerException(String message) {
        super(message);
    }
}
