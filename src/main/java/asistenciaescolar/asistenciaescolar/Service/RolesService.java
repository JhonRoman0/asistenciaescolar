package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Dto.dtoRoles;
import asistenciaescolar.asistenciaescolar.Model.Roles;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolesService {

    @Autowired
    private RepositoryRoles repositoryRoles;

    // Listar solo los roles que no están eliminados (estado != 2)
    public List<Roles> listarTodos() {
        return repositoryRoles.findAll().stream()
                .filter(r -> r.getEstado() != 2)
                .collect(Collectors.toList());
    }

    public Roles crearRol(dtoRoles dto) {
        Roles rol = new Roles();
        rol.setNombreRol(dto.getNombreRol());
        rol.setEstado((short) 1); // 1 = Activo por defecto
        rol.setFechaCreacion(LocalDate.now());
        rol.setColor(dto.getColor());

        return repositoryRoles.save(rol);
    }

    // Método para Actualizar
    public Roles actualizarRol(Integer id, dtoRoles dto) {
        Roles rol = repositoryRoles.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));

        rol.setNombreRol(dto.getNombreRol());
        // El estado se actualiza si el DTO lo trae, si no, se mantiene el actual
        if (dto.getEstado() != null) {
            rol.setEstado(dto.getEstado());
        }
        if (dto.getColor()!=null){
            rol.setColor(dto.getColor());
        }


        return repositoryRoles.save(rol);
    }

    // Método para Eliminación Lógica (Estado 2)
    public void eliminarLogico(Integer id) {
        Roles rol = repositoryRoles.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));

        rol.setEstado((short) 2); // Cambiamos a estado 2 para "eliminar" sin borrar de la DB
        repositoryRoles.save(rol);
    }
}