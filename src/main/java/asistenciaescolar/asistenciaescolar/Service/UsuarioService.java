package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Dto.dtoUsuario;
import asistenciaescolar.asistenciaescolar.Model.Roles;
import asistenciaescolar.asistenciaescolar.Model.Usuario;
import asistenciaescolar.asistenciaescolar.Model.UsuarioRoles;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryRoles;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryUsuario;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryUsuarioRoles;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final RepositoryUsuario repositoryUsuario;
    private final PasswordEncoder passwordEncoder;
    private final RepositoryRoles repositoryRoles;
    private final RepositoryUsuarioRoles repositoryUsuarioRoles;

    @Transactional
    public List<Usuario> listarTodos() {
        return repositoryUsuario.findAll();
    }

    @Transactional
    public Usuario obtenerPorId(Integer id) {
        return repositoryUsuario.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con el ID: " + id));
    }

    @Transactional
    public List<Roles> obtenerRolesDisponibles() {
        return repositoryRoles.findAll();
    }

    @Transactional
    public Usuario crearUsuarioDesdeDto(dtoUsuario dto) {
        // 1. VALIDACIÓN DE DUPLICADOS
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico es obligatorio.");
        }
        if (repositoryUsuario.existsByEmail(dto.getEmail().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo electrónico ya está registrado.");
        }

        // 2. Mapear DTO a la Entidad Usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellidoPaterno(dto.getApellidoPaterno());
        usuario.setApellidoMaterno(dto.getApellidoMaterno());
        usuario.setEmail(dto.getEmail());
        usuario.setContraseña(passwordEncoder.encode(dto.getContraseña()));
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setEstado(dto.getEstado() != null ? dto.getEstado() : (short) 1);

        // 3. Obtener letra del primer rol para el código generado de forma dinámica
        String letraRol = obtenerLetraPrimerRol(dto.getRolesIds());
        usuario.setCodigUsuario(generarCodigoConRol(usuario, letraRol));

        // 4. GUARDAR USUARIO
        Usuario usuarioGuardado = repositoryUsuario.save(usuario);

        // 5. VINCULAR ROLES
        vincularRoles(usuarioGuardado, dto.getRolesIds());

        return usuarioGuardado;
    }

    @Transactional
    public Usuario actualizarUsuarioDesdeDto(Integer id, dtoUsuario dto) {
        // 1. Obtener el usuario existente o lanzar 404
        Usuario usuarioExistente = obtenerPorId(id);

        // 2. Manejo de contraseña (solo si se envía una nueva en el DTO)
        if (dto.getContraseña() != null && !dto.getContraseña().trim().isEmpty()) {
            usuarioExistente.setContraseña(passwordEncoder.encode(dto.getContraseña()));
        }

        // 3. Actualizar datos personales desde el DTO
        usuarioExistente.setNombre(dto.getNombre());
        usuarioExistente.setApellidoPaterno(dto.getApellidoPaterno());
        usuarioExistente.setApellidoMaterno(dto.getApellidoMaterno());
        usuarioExistente.setEmail(dto.getEmail());
        if (dto.getEstado() != null) {
            usuarioExistente.setEstado(dto.getEstado());
        }

        // 4. Actualizar Roles (Limpiar y Reasignar si viene la lista)
        if (dto.getRolesIds() != null) {
            repositoryUsuarioRoles.deleteByUsuario(usuarioExistente);
            // ¡AÑADE ESTA LÍNEA! Limpia la colección en memoria para que no haya datos basura
            if (usuarioExistente.getUsuarioRoles() != null) {
                usuarioExistente.getUsuarioRoles().clear();
            }
            vincularRoles(usuarioExistente, dto.getRolesIds());
        }

        return repositoryUsuario.save(usuarioExistente);
    }

    private void vincularRoles(Usuario usuario, List<Integer> rolesIds) {
        if (rolesIds != null && !rolesIds.isEmpty()) {
            List<UsuarioRoles> vinculaciones = new ArrayList<>();
            for (Integer rolId : rolesIds) {
                Roles rol = repositoryRoles.findById(rolId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol con ID " + rolId + " no encontrado"));

                UsuarioRoles ur = new UsuarioRoles();
                ur.setUsuario(usuario);
                ur.setRol(rol);
                ur.setFechaAsignacion(LocalDate.now());
                vinculaciones.add(ur);
            }
            repositoryUsuarioRoles.saveAll(vinculaciones);
        }
    }

    private String obtenerLetraPrimerRol(List<Integer> rolesIds) {
        if (rolesIds != null && !rolesIds.isEmpty()) {
            return repositoryRoles.findById(rolesIds.get(0))
                    .map(r -> r.getNombreRol().substring(0, 1).toUpperCase())
                    .orElse("U");
        }
        return "U";
    }

    @Transactional(readOnly = true)
    public Usuario obtenerPorCodigoUsuario(String codigUsuario) {
        return repositoryUsuario.findByCodigUsuario(codigUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con el código: " + codigUsuario));
    }

    private String generarCodigoConRol(Usuario u, String letraRol) {
        String n = (u.getNombre() != null && !u.getNombre().isEmpty())
                ? u.getNombre().substring(0, 1).toUpperCase() : "X";

        String ap = (u.getApellidoPaterno() != null && !u.getApellidoPaterno().isEmpty())
                ? u.getApellidoPaterno().substring(0, 1).toUpperCase() : "X";

        String anio = String.valueOf(LocalDateTime.now().getYear()).substring(2);
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        return letraRol + n + ap + anio + randomPart;
    }

    @Transactional
    public void eliminarLogico(Integer id) {
        // Reutilizamos obtenerPorId (Asegura el control del 404)
        Usuario usuario = obtenerPorId(id);

        usuario.setEstado((short) 2); // 2 = Eliminado/Inactivo permanentemente
        repositoryUsuario.save(usuario);
    }
}