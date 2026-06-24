package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Dto.dtoRoles;
import asistenciaescolar.asistenciaescolar.Model.Modulo;
import asistenciaescolar.asistenciaescolar.Model.Roles;
import asistenciaescolar.asistenciaescolar.Model.RolesModulo;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryModulo;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryRoles;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryRolesModulo;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryUsuarioRoles;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolesService {

    private final RepositoryRoles repositoryRoles;
    private final RepositoryRolesModulo repositoryRolesModulo;
    private final RepositoryUsuarioRoles repositoryUsuarioRoles;
    private final RepositoryModulo repositoryModulo;

    // Listar solo los roles que no están eliminados (estado != 2)
    public List<Roles> listarTodos() {
        return repositoryRoles.findAll().stream()
                .filter(r -> r.getEstado() != 2)
                .collect(Collectors.toList());
    }

    @Transactional
    public Roles buscarPorId(Integer id) {
        return repositoryRoles.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado con el ID: " + id));
    }

    @Transactional
    public Roles crearRol(dtoRoles dto) {
        if (dto.getNombreRol() == null || dto.getNombreRol().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del rol es obligatorio.");
        }
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
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Módulo de acceso no encontrado con ID: " + idModulo));

                RolesModulo relacion = new RolesModulo();
                relacion.setRol(rolGuardado);
                relacion.setModulo(modulo);
                repositoryRolesModulo.save(relacion);
            }
        }

        return rolGuardado;
    }

    // Método para Actualizar
    @Transactional
    public Roles actualizarRol(Integer id, dtoRoles dto) {
        if (dto.getNombreRol() == null || dto.getNombreRol().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del rol no puede estar vacío.");
        }
        Roles rol = buscarPorId(id);
        rol.setNombreRol(dto.getNombreRol());
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
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Módulo de acceso no encontrado con ID: " + idModulo));

                RolesModulo relacion = new RolesModulo();
                relacion.setRol(rolActualizado);
                relacion.setModulo(modulo);
                repositoryRolesModulo.save(relacion);
            }
        }


        return rolActualizado;
    }

    // Método para Eliminación Lógica (Estado 2)
    @Transactional
    public void eliminarLogico(Integer id) {
        // Busca si el rol existe en la base de datos
        Roles rol = buscarPorId(id);

        // VALIDACIÓN: Contamos cuántas filas usan este objeto Rol en la tabla intermedia
        long cantidadUsuarios = repositoryUsuarioRoles.countByRol(rol);

        if (cantidadUsuarios > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede eliminar el rol '" + rol.getNombreRol() + "' porque tiene " + cantidadUsuarios + " usuario(s) asignado(s).");
        }

        // Si está libre de usuarios, hacemos el borrado lógico (Estado 2)
        rol.setEstado((short) 0);
        repositoryRoles.save(rol);
    }
}