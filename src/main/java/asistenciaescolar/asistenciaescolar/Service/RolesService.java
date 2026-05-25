package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Dto.dtoRoles;
import asistenciaescolar.asistenciaescolar.Model.Modulo;
import asistenciaescolar.asistenciaescolar.Model.Roles;
import asistenciaescolar.asistenciaescolar.Model.RolesModulo;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryModulo;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryRoles;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryRolesModulo;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryUsuarioRoles;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolesService {

    @Autowired
    private RepositoryRoles repositoryRoles;

    @Autowired
    private RepositoryRolesModulo repositoryRolesModulo;

    @Autowired
    private RepositoryUsuarioRoles repositoryUsuarioRoles;

    @Autowired
    private RepositoryModulo repositoryModulo;

    // Listar solo los roles que no están eliminados (estado != 2)
    public List<Roles> listarTodos() {
        return repositoryRoles.findAll().stream()
                .filter(r -> r.getEstado() != 2)
                .collect(Collectors.toList());
    }

    @Transactional
    public Roles crearRol(dtoRoles dto) {
        Roles rol = new Roles();
        rol.setNombreRol(dto.getNombreRol());
        rol.setEstado((short) 1); // 1 = Activo por defecto
        rol.setFechaCreacion(LocalDate.now());
        // Controlamos que si el color llega nulo o vacío, use el azul por defecto del DTO
        if (dto.getColor() == null || dto.getColor().trim().isEmpty()) {
            rol.setColor("#4361EE");
        } else {
            rol.setColor(dto.getColor());
        }

        Roles rolGuardado = repositoryRoles.save(rol);
        if (dto.getIdModulos() != null && !dto.getIdModulos().isEmpty()) {
            for (Integer idModulo : dto.getIdModulos()) {
                Modulo modulo = repositoryModulo.findById(idModulo)
                        .orElseThrow(() -> new RuntimeException("Módulo no encontrado con ID: " + idModulo));

                RolesModulo relacion = new RolesModulo();
                relacion.setRol(rolGuardado);
                relacion.setModulo(modulo);
                repositoryRolesModulo.save(relacion);
            }
        }

        return rolGuardado;
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
        Roles rolActualizado = repositoryRoles.save(rol);

        // 2. Actualizar los módulos: Enfoque "Limpiar y volver a crear"
        if (dto.getIdModulos() != null) {
            // Borramos los accesos antiguos que tenía este rol
            repositoryRolesModulo.deleteByRolIdRoles(id);

            // Insertamos los nuevos accesos que vienen del formulario
            for (Integer idModulo : dto.getIdModulos()) {
                Modulo modulo = repositoryModulo.findById(idModulo)
                        .orElseThrow(() -> new RuntimeException("Módulo no encontrado con ID: " + idModulo));

                RolesModulo relacion = new RolesModulo();
                relacion.setRol(rolActualizado);
                relacion.setModulo(modulo);
                repositoryRolesModulo.save(relacion);
            }
        }


        return rolActualizado;
    }

    // Método para Eliminación Lógica (Estado 2)
    public void eliminarLogico(Integer id) {
        // Busca si el rol existe en la base de datos
        Roles rol = repositoryRoles.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));

        // VALIDACIÓN: Contamos cuántas filas usan este objeto Rol en la tabla intermedia
        long cantidadUsuarios = repositoryUsuarioRoles.countByRol(rol);

        if (cantidadUsuarios > 0) {
            throw new RuntimeException("No se puede eliminar el rol '" + rol.getNombreRol() +
                    "' porque tiene " + cantidadUsuarios + " usuario(s) asignado(s).");
        }

        // Si está libre de usuarios, hacemos el borrado lógico (Estado 2)
        rol.setEstado((short) 2);
        repositoryRoles.save(rol);
    }
}