package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Dto.dtoAsistenciaRequest;
import asistenciaescolar.asistenciaescolar.Dto.dtoAsistenciaResponse;
import asistenciaescolar.asistenciaescolar.Service.AsistenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/asistencias")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Asistencia", description = "Controlador para el flujo de marcado de asistencia por QR")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    // 1. Next.js lee el QR y llama a esto para pintar la pantalla de validación del estudiante
    @PostMapping("/previsualizar")
    @Operation(summary = "Previsualizar datos del alumno mediante lectura de QR")
    public ResponseEntity<dtoAsistenciaResponse> previsualizar(@RequestBody dtoAsistenciaRequest request) {
        // Sin try-catch: si el código QR es inválido o no existe, el servicio lanza un error controlado
        dtoAsistenciaResponse datosAlumno = asistenciaService.previsualizarAlumno(request);
        return ResponseEntity.ok(datosAlumno);
    }

    // 2. Cuando el usuario le da al botón "Confirmar" en Next.js, se llama a este para grabar en la BD
    @PostMapping("/confirmar")
    @Operation(summary = "Confirmar y registrar la asistencia en la base de datos")
    public ResponseEntity<dtoAsistenciaResponse> confirmarRegistro(@RequestBody dtoAsistenciaRequest request) {
        // Retorna HttpStatus.CREATED (201) porque estamos insertando un nuevo registro de asistencia
        dtoAsistenciaResponse resultado = asistenciaService.registrarAsistenciaConfirmada(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

}