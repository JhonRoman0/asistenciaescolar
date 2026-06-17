package asistenciaescolar.asistenciaescolar.Config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Esta clase indica que, al lanzarse, se debe reportar como un 503 Service Unavailable
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class DniApiException extends RuntimeException {
    public DniApiException(String message) {
        super(message);
    }
}