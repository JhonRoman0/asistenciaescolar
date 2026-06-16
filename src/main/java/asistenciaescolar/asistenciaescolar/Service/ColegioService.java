package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Dto.dtoColegio;
import asistenciaescolar.asistenciaescolar.Dto.dtoTurno;
import asistenciaescolar.asistenciaescolar.Model.Colegio;
import asistenciaescolar.asistenciaescolar.Model.Turno;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryColegio;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryTurno;
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

    @Transactional
    public Colegio registrarColegioYTurnos(dtoColegio dto) {
        if (dto.getColegio() == null || dto.getColegio().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del colegio es obligatorio.");
        }
        // 1. Guardar el Colegio
        Colegio colegio = new Colegio();
        colegio.setColegio(dto.getColegio());
        colegio.setCodigo(dto.getCodigo());
        colegio.setDireccion(dto.getDireccion());
        colegio.setTelefono(dto.getTelefono());
        colegio.setCelular(dto.getCelular());
        colegio.setGmail(dto.getGmail());
        Colegio colegioGuardado = repositoryColegio.save(colegio);

        // 2. Guardar cada turno de la lista de forma masiva
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

    // ACTUALIZAR
    @Transactional
    public Colegio actualizarColegioYTurnos(Integer id, dtoColegio dto) {
        // 1. Verificar si el colegio existe y actualizar sus datos básicos
        Colegio colegioExistente = obtenerPorId(id);

        colegioExistente.setColegio(dto.getColegio());
        colegioExistente.setCodigo(dto.getCodigo());
        colegioExistente.setDireccion(dto.getDireccion());
        colegioExistente.setTelefono(dto.getTelefono());
        colegioExistente.setCelular(dto.getCelular());
        colegioExistente.setGmail(dto.getGmail());

        Colegio colegioActualizado = repositoryColegio.save(colegioExistente);

        // 2. Limpiar/Eliminar ÚNICAMENTE los turnos que pertenecen a este colegio
        // NOTA: Si en tu RepositoryTurno tienes un método como deleteByColegio o deleteByColegioId, úsalo aquí.
        // Si no tienes configurada la relación, por ahora lo ideal es limpiar solo lo asociado.
        // Como medida temporal segura si no hay relación: repositoryTurno.deleteAll(); // <-- SOLO si el sistema es monocolegio.
        repositoryTurno.deleteAll();

        // 3. Insertar la nueva lista de turnos que viene del Frontend
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

        return colegioActualizado;
    }
}
