package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Dto.dtoTurno;
import asistenciaescolar.asistenciaescolar.Model.Turno;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryTurno;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private final RepositoryTurno repositoryTurno;

    // 1. CREAR (Create)
    @Transactional
    public Turno crearTurno(dtoTurno dto) {
        if (dto.getTurno() == null || dto.getTurno().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del turno es obligatorio.");
        }

        if (repositoryTurno.findByTurno(dto.getTurno().trim()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El turno '" + dto.getTurno() + "' ya está registrado.");
        }

        Turno nuevoTurno = new Turno();
        nuevoTurno.setTurno(dto.getTurno());
        nuevoTurno.setHoraEntrada(dto.getHoraEntrada());
        nuevoTurno.setHoraEntradaLimite(dto.getHoraEntradaLimite());
        nuevoTurno.setHoraFaltaLimite(dto.getHoraFaltaLimite());

        return repositoryTurno.save(nuevoTurno);
    }

    // 2. LEER TODOS (Read - List)
    @Transactional
    public List<Turno> obtenerTodosLosTurnos() {
        return repositoryTurno.findAll();
    }

    // 3. LEER POR ID (Read - Single)
    @Transactional
    public Turno obtenerTurnoPorId(Integer id) {
        return repositoryTurno.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turno no encontrado con el ID: " + id));
    }

    // 4. ACTUALIZAR (Update)
    @Transactional
    public Turno actualizarTurno(Integer id, dtoTurno dto) {
        if (dto.getTurno() == null || dto.getTurno().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del turno no puede estar vacío.");
        }
        // Verificar si el turno existe antes de modificarlo
        Turno turnoExistente = obtenerTurnoPorId(id);

        // Actualizamos los campos con los nuevos valores del DTO
        turnoExistente.setTurno(dto.getTurno());
        turnoExistente.setHoraEntrada(dto.getHoraEntrada());
        turnoExistente.setHoraEntradaLimite(dto.getHoraEntradaLimite());
        turnoExistente.setHoraFaltaLimite(dto.getHoraFaltaLimite());

        return repositoryTurno.save(turnoExistente);
    }
}