package asistenciaescolar.asistenciaescolar.Service;

// Modelos y Repositorios propios
import asistenciaescolar.asistenciaescolar.Model.Roles;
import asistenciaescolar.asistenciaescolar.Model.Usuario;
import asistenciaescolar.asistenciaescolar.Model.UsuarioRoles;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryRoles;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryUsuario;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryUsuarioRoles;

// Spring Framework y Stereotypes
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Seguridad (BCrypt y PasswordEncoder)
import org.springframework.security.crypto.password.PasswordEncoder;

// Utilidades de Java
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UsuarioService {

    @Autowired
    private RepositoryUsuario repositoryUsuario;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RepositoryRoles repositoryRoles;

    @Autowired
    private RepositoryUsuarioRoles repositoryUsuarioRoles;

    public List<Usuario> listarTodos() {
        return repositoryUsuario.findAll();
    }

    public Usuario obtenerPorId(Integer id) {
        return repositoryUsuario.findById(id).orElse(null);
    }

    // --- FUNCIONALIDAD DE CREACIÓN ---
    @Transactional
    public void crearUsuario(Usuario usuario, List<Integer> rolesIds) {
        // 1. VALIDACIÓN DE DUPLICADOS (Backend)
        if (repositoryUsuario.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está registrado.");
        }

        // 2. Configuraciones básicas
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setEstado((short) 1);

        // 3. Obtener letra del primer rol para el código
        String letraRol = obtenerLetraPrimerRol(rolesIds);
        usuario.setCodigUsuario(generarCodigoConRol(usuario, letraRol));

        // 4. GUARDAR AL USUARIO
        Usuario usuarioGuardado = repositoryUsuario.save(usuario);

        // 5. VINCULAR ROLES
        vincularRoles(usuarioGuardado, rolesIds);
    }

    // --- FUNCIONALIDAD DE ACTUALIZACIÓN ---
    @Transactional
    public void actualizarUsuario(Usuario usuario, List<Integer> rolesIds) {
        Usuario usuarioExistente = repositoryUsuario.findById(usuario.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 1. Manejo de contraseña (solo si se envía una nueva)
        if (usuario.getContraseña() != null && !usuario.getContraseña().isEmpty()) {
            usuarioExistente.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        }

        // 2. Actualizar datos personales
        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setApellidoPaterno(usuario.getApellidoPaterno());
        usuarioExistente.setApellidoMaterno(usuario.getApellidoMaterno());
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setEstado(usuario.getEstado());

        // 3. Actualizar Roles (Limpiar y Reasignar)
        if (rolesIds != null) {
            repositoryUsuarioRoles.deleteByUsuario(usuarioExistente);
            vincularRoles(usuarioExistente, rolesIds);
        }

        repositoryUsuario.save(usuarioExistente);
    }

    // --- FUNCIONES DE APOYO (Privadas) ---

    private void vincularRoles(Usuario usuario, List<Integer> rolesIds) {
        if (rolesIds != null && !rolesIds.isEmpty()) {
            List<UsuarioRoles> vinculaciones = new ArrayList<>();
            for (Integer rolId : rolesIds) {
                Roles rol = repositoryRoles.findById(rolId)
                        .orElseThrow(() -> new RuntimeException("Rol con ID " + rolId + " no encontrado"));

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

    private String generarCodigoConRol(Usuario u, String letraRol) {
        String n = (u.getNombre() != null && !u.getNombre().isEmpty())
                ? u.getNombre().substring(0, 1).toUpperCase() : "X";

        String ap = (u.getApellidoPaterno() != null && !u.getApellidoPaterno().isEmpty())
                ? u.getApellidoPaterno().substring(0, 1).toUpperCase() : "X";

        String anio = String.valueOf(LocalDateTime.now().getYear()).substring(2);
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        return letraRol + n + ap + anio + randomPart;
    }

    public void eliminarLogico(Integer id) {
        Usuario usuario = repositoryUsuario.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setEstado((short)2); // 2 = Eliminado/Inactivo permanentemente
        repositoryUsuario.save(usuario);
    }
}