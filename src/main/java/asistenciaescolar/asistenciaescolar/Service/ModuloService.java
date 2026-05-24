package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Dto.dtoModulo;
import asistenciaescolar.asistenciaescolar.Model.Modulo;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryModulo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ModuloService {

    @Autowired
    private RepositoryModulo repositoryModulo;

    public List<Modulo> listarTodos() {
        return repositoryModulo.findAll();
    }

    public Optional<Modulo> buscarPorId(Integer id) {
        return repositoryModulo.findById(id);
    }

    public Modulo guardar(dtoModulo dto) {
        Modulo modulo = new Modulo();
        modulo.setNombre(dto.getNombre());
        // 1. Estado por defecto en 1 al crearse
        modulo.setEstado((short) 1);
        // Asigna la fecha del día actual de forma automática
        modulo.setFechaCreacion(LocalDate.now());
        return repositoryModulo.save(modulo);
    }

    public Modulo actualizar(Integer id, dtoModulo dto) {
        return repositoryModulo.findById(id).map(modulo -> {
            modulo.setNombre(dto.getNombre());
            modulo.setEstado(dto.getEstado()); // Permite actualizar a otros estados si es necesario
            return repositoryModulo.save(modulo);
        }).orElseThrow(() -> new RuntimeException("Módulo no encontrado con el ID: " + id));
    }

    // 2. Eliminación lógica: Cambia el estado a 2 en lugar de borrarlo físicamente
    public void eliminarLogico(Integer id) {
        repositoryModulo.findById(id).map(modulo -> {
            modulo.setEstado((short) 2);
            return repositoryModulo.save(modulo);
        }).orElseThrow(() -> new RuntimeException("Módulo no encontrado con el ID: " + id));
    }
}
