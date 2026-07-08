package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Dto.dtoReporteAlumno;
import asistenciaescolar.asistenciaescolar.Dto.dtoReporteGeneral;
import asistenciaescolar.asistenciaescolar.Dto.dtoReporteUsuario;
import asistenciaescolar.asistenciaescolar.Service.AsistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private AsistenciaService asistenciaService;

    /**
     * 1. REPORTE GENERAL (Diario, Semanal, Mensual o por Rangos)
     * Si no se envían fechas, por defecto toma el día actual.
     */
    @GetMapping("/general")
    public ResponseEntity<List<dtoReporteGeneral>> getReporteGeneral(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        if (inicio == null) inicio = LocalDate.now();
        if (fin == null) fin = LocalDate.now();

        return ResponseEntity.ok(asistenciaService.generarReporteGeneral(inicio, fin));
    }

    /**
     * 2. REPORTE POR ALUMNO (Historial completo hasta la fecha por defecto)
     * Si no se envían fechas, trae TODO su historial desde el año 2000 hasta hoy.
     */
    @GetMapping("/alumno/{idAlumno}")
    public ResponseEntity<List<dtoReporteAlumno>> getReportePorAlumno(
            @PathVariable Integer idAlumno,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        // Si no mandan fecha de inicio, buscamos desde el año 2000 (trae todo su histórico)
        if (inicio == null) inicio = LocalDate.of(2000, 1, 1);
        if (fin == null) fin = LocalDate.now();

        return ResponseEntity.ok(asistenciaService.generarReportePorAlumno(idAlumno, inicio, fin));
    }

    /**
     * 3. REPORTE POR USUARIO REGISTRADOR (Auditoría completa hasta la fecha por defecto)
     * Si no se envían fechas, trae TODOS los registros que ha procesado en la historia.
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<dtoReporteUsuario>> getReportePorUsuario(
            @PathVariable Integer idUsuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        // Si no mandan fecha de inicio, buscamos desde el año 2000 (trae todo su histórico)
        if (inicio == null) inicio = LocalDate.of(2000, 1, 1);
        if (fin == null) fin = LocalDate.now();

        return ResponseEntity.ok(asistenciaService.generarReportePorUsuario(idUsuario, inicio, fin));
    }
}
