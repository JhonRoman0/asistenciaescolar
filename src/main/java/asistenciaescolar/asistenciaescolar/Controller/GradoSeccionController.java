package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Model.GradoSeccion;
import asistenciaescolar.asistenciaescolar.Service.AlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/grados-secciones")
@RequiredArgsConstructor
@Tag(name = "Grados y Secciones", description = "Catálogo de combinaciones de grados y secciones")
public class GradoSeccionController {

    private final AlumnoService alumnoService;

    @GetMapping
    @Operation(summary = "Listar combinaciones de Grados y Secciones asignadas")
    public ResponseEntity<List<GradoSeccion>> obtenerGradosSecciones() {
        return ResponseEntity.ok(alumnoService.listarGradosSecciones());
    }
}