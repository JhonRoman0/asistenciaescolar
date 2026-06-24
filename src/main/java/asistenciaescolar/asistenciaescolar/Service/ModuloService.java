package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Dto.dtoModulo;
import asistenciaescolar.asistenciaescolar.Model.Modulo;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryModulo;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuloService {

    private final RepositoryModulo repositoryModulo;

    @Transactional
    public List<Modulo> listarTodos() {
        return repositoryModulo.findAll();
    }

    @Transactional
    public Modulo buscarPorId(Integer id) {
        return repositoryModulo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Módulo no encontrado con el ID: " + id));
    }

    @Transactional
    public Modulo guardar(dtoModulo dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del módulo es obligatorio.");
        }
        Modulo modulo = new Modulo();
        modulo.setNombre(dto.getNombre().trim());
        modulo.setEstado((short) 1);
        modulo.setFechaCreacion(LocalDate.now());
        return repositoryModulo.save(modulo);
    }

    @Transactional
    public Modulo actualizar(Integer id, dtoModulo dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del módulo no puede estar vacío.");
        }

        Modulo moduloExistente = buscarPorId(id);

        moduloExistente.setNombre(dto.getNombre().trim());
        moduloExistente.setEstado(dto.getEstado()); // Permite actualizar a otros estados si es necesario

        return repositoryModulo.save(moduloExistente);
    }

    // 2. Eliminación lógica: Cambia el estado a 2 en lugar de borrarlo físicamente
    @Transactional
    public void eliminarLogico(Integer id) {
        // Reutilizamos buscarPorId; si no existe, lanza el 404 controlado de inmediato
        Modulo modulo = buscarPorId(id);

        modulo.setEstado((short) 0); // Cambia el estado a 2 (Inactivo/Eliminado)
        repositoryModulo.save(modulo);
    }
}
