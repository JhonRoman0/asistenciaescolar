package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Dto.dtoAsistenciaRequest;
import asistenciaescolar.asistenciaescolar.Dto.dtoAsistenciaResponse;
import asistenciaescolar.asistenciaescolar.Model.Justificacion;
import asistenciaescolar.asistenciaescolar.Service.AsistenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
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

    // =========================================================================
    // ENDPOINTS DE CONSULTA Y GESTIÓN (REPORTEADOS)
    // =========================================================================

    @GetMapping("/hoy")
    @Operation(summary = "Lista general de alumnos activos y su estado de asistencia de hoy")
    public ResponseEntity<List<Map<String, Object>>> obtenerAsistenciasHoy() {
        return ResponseEntity.ok(asistenciaService.obtenerAsistenciasHoy());
    }

    @GetMapping("/semana")
    @Operation(summary = "Obtener matriz de asistencia semanal (Lunes a Viernes) basada en una fecha")
    public ResponseEntity<List<Map<String, Object>>> obtenerMatrizSemanal(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(asistenciaService.obtenerMatrizSemanal(fecha));
    }

    @GetMapping("/mes")
    @Operation(summary = "Obtener resumen mensual consolidado con ratios y porcentajes")
    public ResponseEntity<List<Map<String, Object>>> obtenerResumenMensual(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(asistenciaService.obtenerResumenMensual(fecha));
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener KPIs globales agrupados para gráficos del Dashboard")
    public ResponseEntity<Map<String, Long>> obtenerEstadisticas(
            @RequestParam(value = "rango", defaultValue = "hoy") String rango,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(asistenciaService.obtenerEstadisticas(rango, fecha));
    }

    @PutMapping("/{id}/justificar")
    @Operation(summary = "Cambiar retroactivamente una inasistencia/tardanza a un estado Justificado")
    public ResponseEntity<Map<String, String>> justificarAsistenciaRetroactiva(
            @PathVariable("id") Integer idAsistencia,
            @RequestParam("idJustificacion") Integer idJustificacion,
            @RequestParam("idUsuarioAdmin") Integer idUsuarioAdmin) {

        asistenciaService.justificarAsistenciaRetroactiva(idAsistencia, idJustificacion, idUsuarioAdmin);

        // Retornamos un JSON descriptivo estructurado en lugar de un String plano
        return ResponseEntity.ok(Map.of("message", "Asistencia justificada de manera exitosa de forma retroactiva."));
    }
    @GetMapping("/justificaciones")
    public ResponseEntity<List<Justificacion>> listarJustificaciones() {
        List<Justificacion> lista = asistenciaService.listarJustificaciones();
        return ResponseEntity.ok(lista);
    }

}