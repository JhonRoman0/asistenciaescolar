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

    // LISTAR TODOS
    @Transactional
    public List<Colegio> obtenerTodos() {
        return repositoryColegio.findAll();
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

        // Limpieza y re-inserción de Turnos
        repositoryTurno.deleteAll();
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

        // Limpieza controlada de combinaciones previas antes de insertar la nueva configuración
        repositoryGradoSeccion.deleteAll();

        // Re-procesar las asignaciones directas enviadas
        procesarGradosYSecciones(dto.getConfiguracionGrados());

        return colegioActualizado;
    }

    // MÉTODO AUXILIAR PRIVADO: Mapea cada grado con sus secciones respectivas evitando duplicados
    private void procesarGradosYSecciones(List<dtoGradoSeccionInput> configuracionGrados) {
        if (configuracionGrados == null || configuracionGrados.isEmpty()) return;

        for (dtoGradoSeccionInput config : configuracionGrados) {
            if (config.getGrado() == null || config.getGrado().trim().isEmpty()) continue;

            String nombreGradoLimpio = config.getGrado().trim();

            // Buscar si ya existe el Grado de forma global para reusarlo, o crearlo.
            Grado grado = repositoryGrado.findAll().stream()
                    .filter(g -> g.getGrado().equalsIgnoreCase(nombreGradoLimpio))
                    .findFirst()
                    .orElseGet(() -> {
                        Grado nuevoG = new Grado();
                        nuevoG.setGrado(nombreGradoLimpio);
                        return repositoryGrado.save(nuevoG);
                    });

            // Procesar solo las secciones que el Frontend le asignó a este grado en particular
            if (config.getSecciones() != null) {
                for (String nomSeccion : config.getSecciones()) {
                    if (nomSeccion == null || nomSeccion.trim().isEmpty()) continue;

                    String nombreSeccionLimpio = nomSeccion.trim();

                    // Buscar si la Sección existe de forma global, o crearla.
                    Seccion seccion = repositorySeccion.findAll().stream()
                            .filter(s -> s.getSeccion().equalsIgnoreCase(nombreSeccionLimpio))
                            .findFirst()
                            .orElseGet(() -> {
                                Seccion nuevaS = new Seccion();
                                nuevaS.setSeccion(nombreSeccionLimpio);
                                return repositorySeccion.save(nuevaS);
                            });

                    // Vincular la combinación exacta en la tabla intermedia
                    GradoSeccion gs = new GradoSeccion();
                    gs.setGrado(grado);
                    gs.setSeccion(seccion);
                    repositoryGradoSeccion.save(gs);
                }
            }
        }
    }
}