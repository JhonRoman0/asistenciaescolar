package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Dto.dtoColegio;
import asistenciaescolar.asistenciaescolar.Dto.dtoTurno;
import asistenciaescolar.asistenciaescolar.Model.Colegio;
import asistenciaescolar.asistenciaescolar.Model.Turno;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryColegio;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryTurno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ColegioService {

    @Autowired
    private RepositoryColegio repositoryColegio;

    @Autowired
    private RepositoryTurno repositoryTurno;

    public Colegio registrarColegioYTurnos(dtoColegio dto) {
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
    public List<Colegio> obtenerTodos() {
        return repositoryColegio.findAll();
    }

    // BUSCAR POR ID
    public Colegio obtenerPorId(Integer id) {
        return repositoryColegio.findById(id)
                .orElseThrow(() -> new RuntimeException("Colegio no encontrado con el ID: " + id));
    }

    // ACTUALIZAR
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

        // 2. Limpiar/Eliminar los turnos anteriores de la base de datos
        // Como tu repositorio hereda de JpaRepository, usamos deleteAll() para vaciar la tabla de turnos vieja
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
