package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Dto.dtoAsistenciaRequest;
import asistenciaescolar.asistenciaescolar.Dto.dtoAsistenciaResponse;
import asistenciaescolar.asistenciaescolar.Service.AsistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/asistencias")
@CrossOrigin(origins = "*")
public class AsistenciaController {

    @Autowired
    private AsistenciaService asistenciaService;

    // 1. Next.js lee el QR y llama a esto para pintar la pantalla de validación del estudiante
    @PostMapping("/previsualizar")
    public ResponseEntity<?> previsualizar(@RequestBody dtoAsistenciaRequest request) {
        try {
            dtoAsistenciaResponse datosAlumno = asistenciaService.previsualizarAlumno(request);
            return ResponseEntity.ok(datosAlumno);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // 2. Cuando el usuario le da al botón "Confirmar" en Next.js, se llama a este para grabar en la BD
    @PostMapping("/confirmar")
    public ResponseEntity<?> confirmarRegistro(@RequestBody dtoAsistenciaRequest request) {
        try {
            dtoAsistenciaResponse resultado = asistenciaService.registrarAsistenciaConfirmada(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}