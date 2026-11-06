package projet.td2.configuration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiControllerHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<MessageError> handleApiException(ApiException ex) {
        MessageError message = MessageError.builder().message(ex.getMessage()).build();
        return new ResponseEntity<>(message, ex.getHttpStatus());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageError> handleNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(f ->
                errorMap.put(f.getField(), f.getDefaultMessage())
        );
        return new ResponseEntity<>(MessageError.builder().errors(errorMap).build(), HttpStatus.BAD_REQUEST);
    }

}