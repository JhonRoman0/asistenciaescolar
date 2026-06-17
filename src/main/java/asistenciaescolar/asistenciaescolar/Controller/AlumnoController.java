package asistenciaescolar.asistenciaescolar.Controller;


import asistenciaescolar.asistenciaescolar.Dto.DniData;
import asistenciaescolar.asistenciaescolar.Dto.DniResponse;
import asistenciaescolar.asistenciaescolar.Dto.dtoAlumno;
import asistenciaescolar.asistenciaescolar.Model.Alumno;
import asistenciaescolar.asistenciaescolar.Model.Grado;
import asistenciaescolar.asistenciaescolar.Model.Seccion;
import asistenciaescolar.asistenciaescolar.Service.AlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Alumnos", description = "Crud de Alumnos y algunso filtros")
public class AlumnoController {

    private final AlumnoService alumnoService;

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


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Registrar alumnos con apoderados y foto de perfil")
    public ResponseEntity<Alumno> registrar(
            @RequestPart("alumno") dtoAlumno dto,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alumnoService.guardarAlumno(dto, foto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Actualizar alumnos con apoderado y nueva foto")
    public ResponseEntity<Alumno> actualizar(
            @PathVariable Integer id,
            @RequestPart("alumno") dtoAlumno dto,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {
        return ResponseEntity.ok(alumnoService.actualizarAlumno(id, dto, foto));
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
    public ResponseEntity<Void> eliminarAlumno(@PathVariable Integer id) {
        alumnoService.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar-dni/{dni}")
    @Operation(summary = "Consulta datos de alumno por DNI")
    public ResponseEntity<DniData> consultarDni(@PathVariable String dni) {
        if (dni == null || dni.length() != 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El DNI debe tener 8 dígitos");
        }
        return ResponseEntity.ok(alumnoService.obtenerDatosPorDni(dni));
    }
}