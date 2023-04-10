package hexlet.code.exception.handler;

import hexlet.code.exception.ApiErrorResponse;
import hexlet.code.exception.NotFoundInDatabaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.StringJoiner;

@ControllerAdvice // Handle exceptions from all controllers
public class GlobalApiExceptionHandler {

    // @Valid exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleRequestValidationException(MethodArgumentNotValidException e,
                                                                             HttpServletRequest request) {
        StringJoiner messageJoiner = new StringJoiner("; ");
        e.getBindingResult().getFieldErrors().forEach(error -> messageJoiner.add(error.getDefaultMessage()));

        ApiErrorResponse apiError = new ApiErrorResponse();
        apiError.setLocalDateTime(LocalDateTime.now());
        apiError.setStatusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
        apiError.setMessage(messageJoiner.toString());
        apiError.setPath(request.getRequestURI());
        return new ResponseEntity<>(apiError, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NotFoundInDatabaseException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(RuntimeException e,
                                                                    HttpServletRequest request) {
        ApiErrorResponse apiError = new ApiErrorResponse();
        apiError.setLocalDateTime(LocalDateTime.now());
        apiError.setStatusCode(HttpStatus.NOT_FOUND.value());
        apiError.setMessage(e.getMessage());
        apiError.setPath(request.getRequestURI());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }
}
