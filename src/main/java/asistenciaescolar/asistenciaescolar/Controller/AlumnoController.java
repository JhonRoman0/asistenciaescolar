package asistenciaescolar.asistenciaescolar.Controller;


import asistenciaescolar.asistenciaescolar.Dto.dtoAlumno;
import asistenciaescolar.asistenciaescolar.Model.Alumno;
import asistenciaescolar.asistenciaescolar.Model.Grado;
import asistenciaescolar.asistenciaescolar.Model.Seccion;
import asistenciaescolar.asistenciaescolar.Service.AlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
@CrossOrigin(origins = "*")
@Tag(name = "Alumnos", description = "Crud de Alumnos y algunso filtros")
public class AlumnoController {

    @Autowired
    private AlumnoService alumnoService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener alumno por ID")
    public ResponseEntity<dtoAlumno> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(alumnoService.obtenerPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar alumnos con filtros")
    public ResponseEntity<List<dtoAlumno>> listarAlumnos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer idGrado,
            @RequestParam(required = false) Integer idSeccion,
            @RequestParam(required = false) Integer estado) {
        return ResponseEntity.ok(alumnoService.listarAlumnosConApoderados(nombre, idGrado, idSeccion, estado));
    }


    @PostMapping
    @Operation(summary = "Registrar alumnos con apoderados")
    public ResponseEntity<Alumno> registrar(@RequestBody dtoAlumno dto) {
        return ResponseEntity.ok(alumnoService.guardarAlumno(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar alumnos con apoderado")
    public ResponseEntity<Alumno> actualizar(@PathVariable Integer id, @RequestBody dtoAlumno dto) {
        return ResponseEntity.ok(alumnoService.actualizarAlumno(id, dto));
    }

    @GetMapping("/grados")
    @Operation(summary = "Listra Grados")
    public ResponseEntity<List<Grado>> obtenerGrados() {
        return ResponseEntity.ok(alumnoService.listarGrados());
    }

    @GetMapping("/secciones")
    @Operation(summary = "Listar Secciones")
    public ResponseEntity<List<Seccion>> obtenerSecciones() {
        return ResponseEntity.ok(alumnoService.listarSecciones());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar alumnos de manera logica")
    public ResponseEntity<String> eliminarAlumno(@PathVariable Integer id) {
        try {
            alumnoService.eliminarLogico(id);
            return ResponseEntity.ok("Alumno dado de baja correctamente (Estado 2)");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar: " + e.getMessage());
        }
    }
}