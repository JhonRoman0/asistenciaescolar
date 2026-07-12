package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Dto.dtoColegio;
import asistenciaescolar.asistenciaescolar.Dto.dtoTurno;
import asistenciaescolar.asistenciaescolar.Dto.dtoGradoSeccionInput;
import asistenciaescolar.asistenciaescolar.Model.*;
import asistenciaescolar.asistenciaescolar.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ColegioService {

    private final RepositoryColegio repositoryColegio;
    private final RepositoryTurno repositoryTurno;
    private final RepositoryGrado repositoryGrado;
    private final RepositorySeccion repositorySeccion;
    private final RepositoryGradoSeccion repositoryGradoSeccion;

    // 1. REGISTRAR COLEGIO, TURNOS Y SU CONFIGURACIÓN DE GRADOS/SECCIONES
    @Transactional
    public Colegio registrarColegioYTurnos(dtoColegio dto) {
        if (dto.getColegio() == null || dto.getColegio().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del colegio es obligatorio.");
        }

        long cantidadColegios = repositoryColegio.count();
        if (cantidadColegios > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un colegio registrado en el sistema.");
        }

        // Guardar datos básicos del Colegio
        Colegio colegio = new Colegio();
        colegio.setColegio(dto.getColegio());
        colegio.setCodigo(dto.getCodigo());
        colegio.setDireccion(dto.getDireccion());
        colegio.setTelefono(dto.getTelefono());
        colegio.setCelular(dto.getCelular());
        colegio.setGmail(dto.getGmail());
        Colegio colegioGuardado = repositoryColegio.save(colegio);

        // Guardar Turnos masivamente
        if (dto.getTurnos() != null) {
            for (dtoTurno tDto : dto.getTurnos()) {
                Turno turno = new Turno();
                turno.setTurno(tDto.getTurno());
                turno.setHoraEntrada(tDto.getHoraEntrada());
                turno.setHoraEntradaLimite(tDto.getHoraEntradaLimite());
                turno.setHoraFaltaLimite(tDto.getHoraFaltaLimite());
                repositoryTurno.save(turno);
            }
        }

        // Procesar la relación jerárquica de Grados y Secciones
        procesarGradosYSecciones(dto.getConfiguracionGrados());

        return colegioGuardado;
    }

    // 1. OBTENER UN COLEGIO POR ID CON SU SHAPE COMPLETO (Va dentro de ColegioService)
    @Transactional
    public dtoColegio obtenerPorIdConDetalle(Integer id) {
        Colegio c = repositoryColegio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Colegio no encontrado con el ID: " + id));

        dtoColegio dto = new dtoColegio();
        dto.setIdColegio(c.getIdColegio());
        dto.setColegio(c.getColegio());
        dto.setCodigo(c.getCodigo());
        dto.setDireccion(c.getDireccion());
        dto.setTelefono(c.getTelefono());
        dto.setCelular(c.getCelular());
        dto.setGmail(c.getGmail());

        // A. Mapear y adjuntar los Turnos actuales
        List<dtoTurno> listaTurnosDto = repositoryTurno.findAll().stream().map(t -> {
            dtoTurno tDto = new dtoTurno();
            tDto.setTurno(t.getTurno());
            tDto.setHoraEntrada(t.getHoraEntrada());
            tDto.setHoraEntradaLimite(t.getHoraEntradaLimite());
            tDto.setHoraFaltaLimite(t.getHoraFaltaLimite());
            return tDto;
        }).toList();
        dto.setTurnos(listaTurnosDto);

        // B. Mapear y adjuntar la Configuración de Grados y Secciones
        List<dtoGradoSeccionInput> listaGradosDto = repositoryGrado.findAll().stream().map(g -> {
            dtoGradoSeccionInput gDto = new dtoGradoSeccionInput();
            gDto.setGrado(g.getGrado());

            // Filtrar las secciones asociadas a este grado específico en la tabla intermedia
            List<String> nombresSecciones = repositoryGradoSeccion.findAll().stream()
                    .filter(gs -> gs.getGrado().getIdGrado().equals(g.getIdGrado()))
                    .map(gs -> gs.getSeccion().getSeccion())
                    .toList();

            gDto.setSecciones(nombresSecciones);
            return gDto;
        }).filter(gDto -> !gDto.getSecciones().isEmpty()).toList();

        dto.setConfiguracionGrados(listaGradosDto);

        return dto;
    }

    // 2. LISTAR TODOS CON DETALLE (Va dentro de ColegioService)
    @Transactional
    public List<dtoColegio> obtenerTodosConDetalle() {
        return repositoryColegio.findAll().stream()
                .map(c -> obtenerPorIdConDetalle(c.getIdColegio()))
                .toList();
    }

    // BUSCAR POR ID
    @Transactional
    public Colegio obtenerPorId(Integer id) {
        return repositoryColegio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Colegio no encontrado con el ID: " + id));
    }

    // 2. ACTUALIZAR COLEGIO, TURNOS Y RELACIONES GRADO-SECCIÓN

    @Transactional
    public Colegio actualizarColegioYTurnos(Integer id, dtoColegio dto) {
        Colegio colegioExistente = obtenerPorId(id);

        colegioExistente.setColegio(dto.getColegio());
        colegioExistente.setCodigo(dto.getCodigo());
        colegioExistente.setDireccion(dto.getDireccion());
        colegioExistente.setTelefono(dto.getTelefono());
        colegioExistente.setCelular(dto.getCelular());
        colegioExistente.setGmail(dto.getGmail());

        Colegio colegioActualizado = repositoryColegio.save(colegioExistente);

        // 1. PROCESAR TURNOS SIN BORRAR TODO (Evita caídas si hay alumnos enlazados)
        if (dto.getTurnos() != null) {
            for (dtoTurno tDto : dto.getTurnos()) {
                // Buscamos si el turno ya existe por nombre (Ignorando mayúsculas/minúsculas)
                Optional<Turno> turnoExistenteOpt = repositoryTurno.findAll().stream()
                        .filter(t -> t.getTurno().equalsIgnoreCase(tDto.getTurno().trim()))
                        .findFirst();

                Turno turno;
                if (turnoExistenteOpt.isPresent()) {
                    // Si ya existe, lo actualizamos conservando su ID original
                    turno = turnoExistenteOpt.get();
                } else {
                    // Si es nuevo, lo instanciamos
                    turno = new Turno();
                    turno.setTurno(tDto.getTurno().trim());
                }
                turno.setHoraEntrada(tDto.getHoraEntrada());
                turno.setHoraEntradaLimite(tDto.getHoraEntradaLimite());
                turno.setHoraFaltaLimite(tDto.getHoraFaltaLimite());
                repositoryTurno.save(turno);
            }
        }

        // 2. RE-PROCESAR GRADOS Y SECCIONES DE FORMA ADITIVA
        procesarGradosYSecciones(dto.getConfiguracionGrados());

        return colegioActualizado;
    }

    // MÉTODO AUXILIAR PRIVADO: Mapea cada grado con sus secciones respectivas evitando duplicados
    private void procesarGradosYSecciones(List<dtoGradoSeccionInput> configuracionGrados) {
        if (configuracionGrados == null || configuracionGrados.isEmpty()) return;

        for (dtoGradoSeccionInput config : configuracionGrados) {
            if (config.getGrado() == null || config.getGrado().trim().isEmpty()) continue;

            String nombreGradoLimpio = config.getGrado().trim();
            Grado grado = repositoryGrado.findAll().stream()
                    .filter(g -> g.getGrado().equalsIgnoreCase(nombreGradoLimpio))
                    .findFirst()
                    .orElseGet(() -> {
                        Grado nuevoG = new Grado();
                        nuevoG.setGrado(nombreGradoLimpio);
                        return repositoryGrado.save(nuevoG);
                    });
            if (config.getSecciones() != null) {
                for (String nomSeccion : config.getSecciones()) {
                    if (nomSeccion == null || nomSeccion.trim().isEmpty()) continue;
                    String nombreSeccionLimpio = nomSeccion.trim();
                    Seccion seccion = repositorySeccion.findAll().stream()
                            .filter(s -> s.getSeccion().equalsIgnoreCase(nombreSeccionLimpio))
                            .findFirst()
                            .orElseGet(() -> {
                                Seccion nuevaS = new Seccion();
                                nuevaS.setSeccion(nombreSeccionLimpio);
                                return repositorySeccion.save(nuevaS);
                            });
                    boolean yaExisteCombinacion = repositoryGradoSeccion.findAll().stream()
                            .anyMatch(gs -> gs.getGrado().getIdGrado().equals(grado.getIdGrado()) &&
                                    gs.getSeccion().getIdSeccion().equals(seccion.getIdSeccion()));
                    if (!yaExisteCombinacion) {
                        GradoSeccion gs = new GradoSeccion();
                        gs.setGrado(grado);
                        gs.setSeccion(seccion);
                        repositoryGradoSeccion.save(gs);
                    }
                }
            }
        }
    }
}