package asistenciaescolar.asistenciaescolar.Controller;


import asistenciaescolar.asistenciaescolar.Dto.dtoAlumno;
import asistenciaescolar.asistenciaescolar.Model.Alumno;
import asistenciaescolar.asistenciaescolar.Model.Grado;
import asistenciaescolar.asistenciaescolar.Model.Seccion;
import asistenciaescolar.asistenciaescolar.Service.AlumnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
@CrossOrigin(origins = "*")
public class AlumnoController {

    @Autowired
    private AlumnoService alumnoService;

    //Llamamos a los alumnos por su Id.
    @GetMapping("/{id}")
    public ResponseEntity<Alumno> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(alumnoService.obtenerPorId(id));
    }

    // LISTAR CON FILTROS (Para tu tabla con buscador y selects)
    @GetMapping
    public ResponseEntity<List<Alumno>> listarAlumnos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer idGrado,
            @RequestParam(required = false) Integer idSeccion,
            @RequestParam(required = false) Integer estado) {
        return ResponseEntity.ok(alumnoService.listarConFiltros(nombre, idGrado, idSeccion, estado));
    }

    // REGISTRAR: El DTO viaja directo al Service
    @PostMapping
    public ResponseEntity<Alumno> registrar(@RequestBody dtoAlumno dto) {
        return ResponseEntity.ok(alumnoService.guardarAlumno(dto));
    }

    // ACTUALIZAR: Pasamos el ID y el DTO con los nuevos datos
    @PutMapping("/{id}")
    public ResponseEntity<Alumno> actualizar(@PathVariable Integer id, @RequestBody dtoAlumno dto) {
        return ResponseEntity.ok(alumnoService.actualizarAlumno(id, dto));
    }

    // CARGA DE SELECTS (Para el Modal de Alumno)
    @GetMapping("/grados")
    public List<Grado> obtenerGrados() {
        return alumnoService.listarGrados();
    }

    @GetMapping("/secciones")
    public List<Seccion> obtenerSecciones() {
        return alumnoService.listarSecciones();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarAlumno(@PathVariable Integer id) {
        try {
            alumnoService.eliminarLogico(id);
            return ResponseEntity.ok("Alumno dado de baja correctamente (Estado 2)");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar: " + e.getMessage());
        }
    }
}