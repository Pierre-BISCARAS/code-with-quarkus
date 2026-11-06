package projet.td2.configuration.exception.types;

import org.springframework.http.HttpStatus;
import projet.td2.configuration.exception.ApiException;

public class NotFoundException extends ApiException {
    public NotFoundException() {
        this("Not found");
    }

    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

}