package host.caddy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CollectionNotFound extends RuntimeException {

    public CollectionNotFound(String message) {
        super(message);
    }
}