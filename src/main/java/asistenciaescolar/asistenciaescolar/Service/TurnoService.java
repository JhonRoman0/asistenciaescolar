package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Dto.dtoTurno;
import asistenciaescolar.asistenciaescolar.Model.Turno;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryTurno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TurnoService {

    @Autowired
    private RepositoryTurno repositoryTurno;

    // 1. CREAR (Create)
    public Turno crearTurno(dtoTurno dto) {
        if (repositoryTurno.findByTurno(dto.getTurno()).isPresent()) {
            throw new RuntimeException("El turno '" + dto.getTurno() + "' ya está registrado.");
        }

        Turno nuevoTurno = new Turno();
        nuevoTurno.setTurno(dto.getTurno());
        nuevoTurno.setHoraEntrada(dto.getHoraEntrada());
        nuevoTurno.setHoraEntradaLimite(dto.getHoraEntradaLimite());
        nuevoTurno.setHoraFaltaLimite(dto.getHoraFaltaLimite());

        return repositoryTurno.save(nuevoTurno);
    }

    // 2. LEER TODOS (Read - List)
    public List<Turno> obtenerTodosLosTurnos() {
        return repositoryTurno.findAll();
    }

    // 3. LEER POR ID (Read - Single)
    public Turno obtenerTurnoPorId(Integer id) {
        return repositoryTurno.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado con el ID: " + id));
    }

    // 4. ACTUALIZAR (Update)
    public Turno actualizarTurno(Integer id, dtoTurno dto) {
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