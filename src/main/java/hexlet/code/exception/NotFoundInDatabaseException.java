package hexlet.code.exception;

public class NotFoundInDatabaseException extends RuntimeException {

    public NotFoundInDatabaseException(String message) {
        super(message);
    }
}
