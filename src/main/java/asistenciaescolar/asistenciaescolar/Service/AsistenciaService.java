package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Dto.dtoAsistenciaRequest;
import asistenciaescolar.asistenciaescolar.Dto.dtoAsistenciaResponse;
import asistenciaescolar.asistenciaescolar.Model.*;
import asistenciaescolar.asistenciaescolar.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;

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

        // Devolvemos los datos para que el Front pinte la foto y nombre del alumno
        return new dtoAsistenciaResponse(
                alumno.getNombre(),
                alumno.getApellidoPaterno(),
                alumno.getRutaFoto(),
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

        // Guardamos físicamente en la BD
        Asistencias asistencia = new Asistencias();
        asistencia.setAlumno(alumno);
        asistencia.setFecha(LocalDate.now());
        asistencia.setHoraEntrada(horaActual);
        asistencia.setEstado(estado);

        // =========================================================================
        // CAMBIO AQUÍ: Controlar dinámicamente la justificación
        // =========================================================================
        if (idEstadoCalculado == 4) {
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

        return new dtoAsistenciaResponse(
                alumno.getNombre(),
                alumno.getApellidoPaterno(),
                alumno.getRutaFoto(),
                estado.getEstado(),
                horaActual.toString()
        );
    }

    // ==========================================
    // MÉTODOS AUXILIARES PARA REUTILIZAR CÓDIGO
    // ==========================================

    private Alumno buscarAlumnoPorRequest(dtoAsistenciaRequest request) {
        if (request.getCodigoHash() != null && !request.getCodigoHash().isEmpty()) {
            return alumnoRepository.findByCodigoHash(request.getCodigoHash())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Código QR no válido."));
        } else if (request.getCodigoUnico() != null && !request.getCodigoUnico().isEmpty()) {
            return alumnoRepository.findByCodigoUnico(request.getCodigoUnico())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Código único de alumno no encontrado."));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe escanear un QR o ingresar un código manual.");
        }
    }

    private Integer calcularIdEstado(LocalTime horaActual, Turno turno) {
        if (horaActual.isBefore(turno.getHoraEntrada()) || horaActual.equals(turno.getHoraEntrada())) {
            return 1; // Puntual
        } else if (horaActual.isAfter(turno.getHoraEntrada()) &&
                (horaActual.isBefore(turno.getHoraEntradaLimite()) || horaActual.equals(turno.getHoraEntradaLimite()))) {
            return 3; // Tardanza
        } else if (horaActual.isAfter(turno.getHoraEntradaLimite()) &&
                (horaActual.isBefore(turno.getHoraFaltaLimite()) || horaActual.equals(turno.getHoraFaltaLimite()))) {
            return 4; // Asistencia Justificada
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registro denegado: El límite de ingreso ha expirado. Es Inasistencia.");
        }
    }
}