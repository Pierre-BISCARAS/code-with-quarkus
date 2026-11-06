package projet.td2.configuration.exception.types;

import projet.td2.configuration.exception.ApiException;
import org.springframework.http.HttpStatus;

public class InternalErrorException extends ApiException {
    public InternalErrorException() {
        this("Internal error");
    }

    public InternalErrorException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}