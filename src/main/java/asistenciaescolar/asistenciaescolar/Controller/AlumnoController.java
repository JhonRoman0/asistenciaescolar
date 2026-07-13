package asistenciaescolar.asistenciaescolar.Controller;


import asistenciaescolar.asistenciaescolar.Dto.DniData;
import asistenciaescolar.asistenciaescolar.Dto.dtoAlumno;
import asistenciaescolar.asistenciaescolar.Model.Alumno;
import asistenciaescolar.asistenciaescolar.Model.GradoSeccion;
import asistenciaescolar.asistenciaescolar.Service.AlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/alumnos")
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
            @RequestParam(required = false) Integer idGradoSeccion,
            @RequestParam(required = false) Integer estado) {
        return ResponseEntity.ok(alumnoService.listarAlumnosConApoderados(nombre, idGradoSeccion, estado));
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

    @GetMapping("/dni/{dni}")
    @Operation(summary = "Buscar alumno en BD local por DNI (incluyendo inactivos) para detección de duplicados")
    public ResponseEntity<dtoAlumno> buscarAlumnoPorDniEnBD(@PathVariable String dni) {
        // 1. Validación de longitud
        if (dni == null || dni.trim().length() != 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El DNI debe tener exactamente 8 dígitos");
        }

        // 2. Buscamos el Optional en el Service
        Optional<Alumno> alumnoOpt = alumnoService.buscarPorDniEnBD(dni);

        // 3. Extraemos la entidad o lanzamos el error 404 si no existe
        Alumno alumno = alumnoOpt.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado en la base de datos local")
        );

        // 4. Mapeamos a DTO de forma segura y retornamos el HTTP 200 OK
        dtoAlumno dto = alumnoService.convertirADto(alumno);
        return ResponseEntity.ok(dto);
    }
}