package asistenciaescolar.asistenciaescolar.Config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    // 1. Captura específicamente cuando usas ResponseStatusException (que es muy práctico)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> manejarResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now().toString());
        respuesta.put("status", ex.getStatusCode().value());
        respuesta.put("error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase());
        respuesta.put("message", ex.getReason());

        return new ResponseEntity<>(respuesta, ex.getStatusCode());
    }

    // 2. (Opcional) Captura cualquier otro error inesperado del servidor para que no muestre la traza fea de Java
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErroresGenericos(Exception ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now().toString());
        respuesta.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()); // 500
        respuesta.put("error", "Internal Server Error");
        respuesta.put("message", "Ocurrió un error inesperado en el servidor: " + ex.getMessage());

        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DniApiException.class)
    public ResponseEntity<Map<String, Object>> manejarDniError(DniApiException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now().toString());
        respuesta.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        respuesta.put("error", "Error en Servicio de Validación");
        respuesta.put("message", ex.getMessage());
        return new ResponseEntity<>(respuesta, HttpStatus.SERVICE_UNAVAILABLE);
    }
}