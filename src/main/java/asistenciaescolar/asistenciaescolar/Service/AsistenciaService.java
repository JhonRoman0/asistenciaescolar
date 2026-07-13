package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Dto.*;
import asistenciaescolar.asistenciaescolar.Model.*;
import asistenciaescolar.asistenciaescolar.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AsistenciaService {

    @Autowired
    private RepositoryAlumno alumnoRepository;
    @Autowired
    private RepositoryAsistencias asistenciaRepository;
    @Autowired
    private RepositoryEstado estadoRepository;
    @Autowired
    private NotificacionService notificacionService;
    @Autowired
    private RepositoryJustificacion justificacionRepository;
    @Autowired
    private RepositoryUsuario usuarioRepository;

    /**
     * PASO 1: Buscar al alumno y pre-calcular su estado para que el Front lo valide en pantalla.
     * NO GUARDA NADA EN LA BASE DE DATOS.
     */
    @Transactional(readOnly = true)
    public dtoAsistenciaResponse previsualizarAlumno(dtoAsistenciaRequest request) {
        // 1. Identificar al alumno (QR Hash o Código Manual)
        Alumno alumno = buscarAlumnoPorRequest(request);

        // 2. Control de doble escaneo diario
        boolean yaAsistio = asistenciaRepository.existsByAlumnoAndFecha(alumno, LocalDate.now());
        if (yaAsistio) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El alumno ya registró su asistencia el día de hoy.");
        }

        // 3. Captura de tiempo y validación de rango de turno
        LocalTime horaActual = LocalTime.now();
        Turno turno = alumno.getTurno();

        LocalTime inicioPermitido = turno.getHoraEntrada().minusMinutes(45);
        if (horaActual.isBefore(inicioPermitido)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registro denegado: Aún no inicia el horario de ingreso para el turno " + turno.getTurno());
        }

        // 4. Calcular qué Estado le correspondería en este momento
        Integer idEstadoCalculado = calcularIdEstado(horaActual, turno);
        Estado estado = estadoRepository.findById(idEstadoCalculado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Error en la configuración de estados."));

        Integer idGradoSeccionVal = (alumno.getGradoSeccion() != null) ? alumno.getGradoSeccion().getIdGradoSeccion() : null;

        // Devolvemos los datos para que el Front pinte la foto y nombre del alumno
        return new dtoAsistenciaResponse(
                alumno.getNombre(),
                alumno.getApellidoPaterno(),
                alumno.getApellidoMaterno(),                     // <-- Agregado
                alumno.getRutaFoto(),
                alumno.getCodigoUnico(),
                alumno.getCodigoHash(),
                idGradoSeccionVal,                // <-- Ajusta según tu entidad Seccion
                turno.getTurno(),                                // <-- Agregado
                estado.getEstado(),
                horaActual.toString()
        );
    }

    /**
     * PASO 2: Confirmación final desde el Frontend.
     * AQUÍ SÍ SE GUARDA EL REGISTRO Y SE ENVÍA EL SMS.
     */
    @Transactional
    public dtoAsistenciaResponse registrarAsistenciaConfirmada(dtoAsistenciaRequest request) {
        Alumno alumno = buscarAlumnoPorRequest(request);

        // Volvemos a validar doble escaneo por seguridad atómica
        boolean yaAsistio = asistenciaRepository.existsByAlumnoAndFecha(alumno, LocalDate.now());
        if (yaAsistio) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El alumno ya registró su asistencia el día de hoy.");
        }

        LocalTime horaActual = LocalTime.now();
        Turno turno = alumno.getTurno();

        // Calculamos el estado definitivo del momento exacto del click
        Integer idEstadoCalculado = calcularIdEstado(horaActual, turno);
        Estado estado = estadoRepository.findById(idEstadoCalculado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Error en la configuración de estados."));

        if (request.getIdUsuarioRegistro() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es obligatorio enviar el ID del usuario que registra la asistencia.");
        }
        Usuario usuario = usuarioRepository.findById(request.getIdUsuarioRegistro())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario registrador no existe o no está logueado."));

        // Guardamos físicamente en la BD
        Asistencias asistencia = new Asistencias();
        asistencia.setAlumno(alumno);
        asistencia.setFecha(LocalDate.now());
        asistencia.setHoraEntrada(horaActual);
        asistencia.setEstado(estado);
        asistencia.setUsuarioRegistro(usuario);

        // =========================================================================
        // CAMBIO AQUÍ: Controlar dinámicamente la justificación
        // =========================================================================
        if (idEstadoCalculado == 3) {
            if (request.getIdJustificacion() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionar un motivo de justificación para registrar el ingreso.");
            }

            Justificacion justificacion = justificacionRepository.findById(request.getIdJustificacion())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El motivo de justificación seleccionado no es válido."));

            asistencia.setJustificacion(justificacion);
        } else {
            asistencia.setJustificacion(null);
        }

        asistenciaRepository.save(asistencia);

        // Disparamos la alerta asíncrona al padre
        notificacionService.enviarAlertaPadre(alumno, asistencia);

        Integer idGradoSeccionVal = (alumno.getGradoSeccion() != null) ? alumno.getGradoSeccion().getIdGradoSeccion() : null;

        return new dtoAsistenciaResponse(
                alumno.getNombre(),
                alumno.getApellidoPaterno(),
                alumno.getApellidoMaterno(),                     // <-- Agregado
                alumno.getRutaFoto(),
                alumno.getCodigoUnico(),
                alumno.getCodigoHash(), // <-- Agregado
                idGradoSeccionVal,
                turno.getTurno(),                                // <-- Agregado
                estado.getEstado(),
                horaActual.toString()
        );
    }
    // =========================================================================
    // IMPLEMENTACIÓN DE LOS 5 ENDPOINTS PEDIDOS POR EL FRONT
    // =========================================================================

    /**
     * 1. GET /api/asistencias/hoy
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerAsistenciasHoy() {
        LocalDate hoy = LocalDate.now();
        List<Alumno> alumnosActivos = alumnoRepository.findByEstado(1);

        return alumnosActivos.stream().map(alumno -> {
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("idAlumno", alumno.getIdAlumno());
            fila.put("nombreCompleto", alumno.getNombre() + " " + alumno.getApellidoPaterno());
            fila.put("idGradoSeccion", alumno.getGradoSeccion() != null ? alumno.getGradoSeccion().getIdGradoSeccion() : null);

            Optional<Asistencias> asistenciaHoy = asistenciaRepository.findByAlumnoAndFecha(alumno, hoy);

            if (asistenciaHoy.isPresent()) {
                fila.put("idAsistencia", asistenciaHoy.get().getIdAsistencias());
                fila.put("estado", asistenciaHoy.get().getEstado().getEstado());
                fila.put("horaEntrada", asistenciaHoy.get().getHoraEntrada());
                fila.put("marcadoPor", asistenciaHoy.get().getUsuarioRegistro().getNombre());
            } else {
                fila.put("idAsistencia", null);
                fila.put("estado", "Inasistencia");
                fila.put("horaEntrada", null);
                fila.put("marcadoPor", null);
            }
            return fila;
        }).collect(Collectors.toList());
    }

    /**
     * 2. GET /api/asistencias/semana?fecha=YYYY-MM-DD
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerMatrizSemanal(LocalDate fecha) {
        LocalDate lunes = fecha.with(DayOfWeek.MONDAY);
        List<Alumno> alumnos = alumnoRepository.findByEstado(1);

        return alumnos.stream().map(alumno -> {
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("idAlumno", alumno.getIdAlumno());
            fila.put("alumno", alumno.getNombre() + " " + alumno.getApellidoPaterno());

            List<String> estadosSemana = new ArrayList<>();
            for (int i = 0; i < 5; i++) { // Lunes a Viernes
                LocalDate diaEvaluar = lunes.plusDays(i);
                Optional<Asistencias> ast = asistenciaRepository.findByAlumnoAndFecha(alumno, diaEvaluar);
                // Corregido: Obtenemos el nombre del estado desde el objeto Estado correlacionado
                estadosSemana.add(ast.isPresent() ? ast.get().getEstado().getEstado() : "Inasistencia");
            }
            fila.put("estados", estadosSemana);
            return fila;
        }).collect(Collectors.toList());
    }

    /**
     * 3. GET /api/asistencias/mes?fecha=YYYY-MM-DD
     * Envía un resumen por alumno detallando días asistidos, inasistencias y su porcentaje de asistencia.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerResumenMensual(LocalDate fecha) {
        LocalDate inicioMes = fecha.withDayOfMonth(1);
        LocalDate finMes = fecha.withDayOfMonth(fecha.lengthOfMonth());

        List<Alumno> alumnos = alumnoRepository.findByEstado(1);
        // Traemos todas las asistencias del mes de golpe para procesar rápido en memoria
        List<Asistencias> asistenciasMes = asistenciaRepository.findByFechaBetween(inicioMes, finMes);

        // Mapeamos asistencia por ID de Alumno para búsquedas eficientes O(1)
        Map<Integer, List<Asistencias>> asistenciasPorAlumno = asistenciasMes.stream()
                .collect(Collectors.groupingBy(a -> a.getAlumno().getIdAlumno()));

        // Contamos días hábiles reales de Lunes a Viernes en este mes específico
        long diasHabilesDelMes = inicioMes.datesUntil(finMes.plusDays(1))
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY)
                .count();

        if (diasHabilesDelMes == 0) diasHabilesDelMes = 1; // Prevenir división por cero

        final long totalDias = diasHabilesDelMes;

        return alumnos.stream().map(alumno -> {
            List<Asistencias> susAsistencias = asistenciasPorAlumno.getOrDefault(alumno.getIdAlumno(), new ArrayList<>());

            // Consideramos "Asistió" a Puntual(1), Tardanza(3) y Justificada(4)
            long asistenciasRegistradas = susAsistencias.stream()
                    .filter(a -> Arrays.asList(1, 3, 4).contains(a.getEstado().getIdEstado()))
                    .count();

            long inasistencias = totalDias - asistenciasRegistradas;
            double porcentaje = ((double) asistenciasRegistradas / totalDias) * 100;

            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("idAlumno", alumno.getIdAlumno());
            fila.put("alumno", alumno.getNombre() + " " + alumno.getApellidoPaterno());
            fila.put("diasAsistidos", asistenciasRegistradas);
            fila.put("inasistencias", inasistencias < 0 ? 0 : inasistencias);
            fila.put("porcentajeAsistencia", Math.round(porcentaje * 100.0) / 100.0); // Redondeo a 2 decimales

            return fila;
        }).collect(Collectors.toList());
    }

    /**
     * 4. GET /api/asistencias/estadisticas?rango=...&fecha=...
     */
    @Transactional(readOnly = true)
    public Map<String, Long> obtenerEstadisticas(String rango, LocalDate fecha) {
        LocalDate inicio = fecha;
        LocalDate fin = fecha;

        if ("semana".equalsIgnoreCase(rango)) {
            inicio = fecha.with(DayOfWeek.MONDAY);
            fin = fecha.with(DayOfWeek.FRIDAY);
        } else if ("mes".equalsIgnoreCase(rango)) {
            inicio = fecha.withDayOfMonth(1);
            fin = fecha.withDayOfMonth(fecha.lengthOfMonth());
        }

        List<Asistencias> registros = asistenciaRepository.findByFechaBetween(inicio, fin);
        long totalAlumnosActivos = alumnoRepository.findByEstado(1).size();

        // Días laborables en el rango
        long diasRango = inicio.datesUntil(fin.plusDays(1))
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY)
                .count();
        if (diasRango == 0) diasRango = 1;

        long universoTotalEsperado = totalAlumnosActivos * diasRango;

        long puntuales = registros.stream().filter(a -> a.getEstado().getIdEstado() == 1).count();
        long tardanzas = registros.stream().filter(a -> a.getEstado().getIdEstado() == 2).count();
        long justificados = registros.stream().filter(a -> a.getEstado().getIdEstado() == 3).count();

        // Inasistencias reales calculadas matemáticamente
        long totalPresentes = registros.size();
        long inasistencias = universoTotalEsperado - totalPresentes;

        Map<String, Long> kpis = new LinkedHashMap<>();
        kpis.put("Presentes", puntuales);
        kpis.put("Tardanzas", tardanzas);
        kpis.put("Justificados", justificados);
        kpis.put("Inasistencias", inasistencias < 0 ? 0L : inasistencias);

        return kpis;
    }

    /**
     * 5. PUT /api/asistencias/{id}/justificar
     */
    @Transactional
    public void justificarAsistenciaRetroactiva(Integer idAsistencia, Integer idJustificacion, Integer idUsuarioAdmin) {
        Asistencias asistencia = asistenciaRepository.findById(idAsistencia)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro de asistencia no encontrado."));

        Justificacion jst = justificacionRepository.findById(idJustificacion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Motivo de justificación inválido."));

        Usuario admin = usuarioRepository.findById(idUsuarioAdmin)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario administrador no existe."));

        Estado estadoJustificado = estadoRepository.findById(4)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Configuración de estados rota."));

        asistencia.setEstado(estadoJustificado);
        asistencia.setJustificacion(jst);
        asistencia.setUsuarioRegistro(admin);

        asistenciaRepository.save(asistencia);
    }

    // ==========================================
    // MÉTODOS AUXILIARES PARA REUTILIZAR CÓDIGO
    // ==========================================

    private Alumno buscarAlumnoPorRequest(dtoAsistenciaRequest request) {
        Alumno alumno;

        if (request.getCodigoHash() != null && !request.getCodigoHash().isEmpty()) {
            alumno = alumnoRepository.findByCodigoHash(request.getCodigoHash())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Código QR no válido."));
        } else if (request.getCodigoUnico() != null && !request.getCodigoUnico().isEmpty()) {
            alumno = alumnoRepository.findByCodigoUnico(request.getCodigoUnico())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Código único de alumno no encontrado."));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe escanear un QR o ingresar un código manual.");
        }

        if (alumno.getEstado() == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso Denegado: El alumno ha sido dado de baja del sistema.");
        } else if (alumno.getEstado() == 2) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso Denegado: El alumno se encuentra suspendido actualmente.");
        }
        return alumno;
    }

    private Integer calcularIdEstado(LocalTime horaActual, Turno turno) {
        if (horaActual.isBefore(turno.getHoraEntrada()) || horaActual.equals(turno.getHoraEntrada())) {
            return 1; // Puntual
        } else if (horaActual.isAfter(turno.getHoraEntrada()) &&
                (horaActual.isBefore(turno.getHoraEntradaLimite()) || horaActual.equals(turno.getHoraEntradaLimite()))) {
            return 2; // Tardanza
        } else if (horaActual.isAfter(turno.getHoraEntradaLimite()) &&
                (horaActual.isBefore(turno.getHoraFaltaLimite()) || horaActual.equals(turno.getHoraFaltaLimite()))) {
            return 3; // Asistencia Justificada
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registro denegado: El límite de ingreso ha expirado. Es Inasistencia.");
        }
    }
    @Transactional(readOnly = true)
    public List<Justificacion> listarJustificaciones() {
        return justificacionRepository.findAll();
    }

    /**
     * 1. REPORTE GENERAL (Diario, Semanal, Mensual o por Fechas)
     */
    @Transactional(readOnly = true)
    public List<dtoReporteGeneral> generarReporteGeneral(LocalDate inicio, LocalDate fin) {
        List<Asistencias> asistencias = asistenciaRepository.findByFechaBetweenOrderByFechaAscHoraEntradaAsc(inicio, fin);

        return asistencias.stream().map(a -> {
            String gradoSeccion = (a.getAlumno().getGradoSeccion() != null)
                    ? a.getAlumno().getGradoSeccion().getGrado().getGrado() + " - " + a.getAlumno().getGradoSeccion().getSeccion().getSeccion()
                    : "Sin Asignar";

            String justificacion = (a.getJustificacion() != null) ? a.getJustificacion().getDescripcion() : "-";
            String nombreAlumno = a.getAlumno().getNombre() + " " + a.getAlumno().getApellidoPaterno() + " " + a.getAlumno().getApellidoMaterno();

            return new dtoReporteGeneral(
                    a.getFecha(),
                    a.getHoraEntrada(),
                    a.getAlumno().getCodigoUnico(),
                    nombreAlumno,
                    gradoSeccion,
                    a.getEstado().getEstado(),
                    justificacion,
                    a.getUsuarioRegistro().getNombre()
            );
        }).collect(Collectors.toList());
    }

    /**
     * 2. REPORTE POR ALUMNO (Historial detallado con justificaciones)
     */
    @Transactional(readOnly = true)
    public List<dtoReporteAlumno> generarReportePorAlumno(Integer idAlumno, LocalDate inicio, LocalDate fin) {
        // Validar si el alumno existe primero
        if (!alumnoRepository.existsById(idAlumno)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado.");
        }

        List<Asistencias> asistencias = asistenciaRepository.findByAlumno_IdAlumnoAndFechaBetweenOrderByFechaAsc(idAlumno, inicio, fin);

        return asistencias.stream().map(a -> {
            String justificacion = (a.getJustificacion() != null) ? a.getJustificacion().getDescripcion(): "-";
            return new dtoReporteAlumno(
                    a.getFecha(),
                    a.getHoraEntrada(),
                    a.getEstado().getEstado(),
                    justificacion,
                    a.getUsuarioRegistro().getNombre()
            );
        }).collect(Collectors.toList());
    }

    /**
     * 3. REPORTE POR USUARIO REGISTRADOR (Auditoría de ingresos y justificaciones aceptadas)
     */
    @Transactional(readOnly = true)
    public List<dtoReporteUsuario> generarReportePorUsuario(Integer idUsuario, LocalDate inicio, LocalDate fin) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario registrador no encontrado.");
        }

        List<Asistencias> asistencias = asistenciaRepository.findByUsuarioRegistro_IdUsuarioAndFechaBetweenOrderByFechaAsc(idUsuario, inicio, fin);

        return asistencias.stream().map(a -> {
            String gradoSeccion = (a.getAlumno().getGradoSeccion() != null)
                    ? a.getAlumno().getGradoSeccion().getGrado().getGrado() + " - " + a.getAlumno().getGradoSeccion().getSeccion().getSeccion()
                    : "Sin Asignar";

            String justificacion = (a.getJustificacion() != null) ? a.getJustificacion().getDescripcion() : "-";
            String nombreAlumno = a.getAlumno().getNombre() + " " + a.getAlumno().getApellidoPaterno() + " " + a.getAlumno().getApellidoMaterno();

            return new dtoReporteUsuario(
                    a.getFecha(),
                    a.getHoraEntrada(),
                    nombreAlumno,
                    gradoSeccion,
                    a.getEstado().getEstado(),
                    justificacion
            );
        }).collect(Collectors.toList());
    }
}