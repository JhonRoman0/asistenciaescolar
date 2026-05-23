package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Model.Roles;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryRoles;
import asistenciaescolar.asistenciaescolar.Service.UsuarioService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import asistenciaescolar.asistenciaescolar.Model.Usuario;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryUsuario;
import org.springframework.security.core.Authentication;
import java.util.List;
import asistenciaescolar.asistenciaescolar.Dto.dtoUsuario;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RepositoryRoles repositoryRoles;

    @Autowired
    private RepositoryUsuario repositoryUsuario;

    // LISTAR TODOS
    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    // Obtener roles para llenar los checkboxes del HTML
    @GetMapping("/roles-disponibles")
    public ResponseEntity<List<Roles>> obtenerRolesParaRegistro() {
        return ResponseEntity.ok(repositoryRoles.findAll());
    }

    // REGISTRO (CREAR) usando el DTO
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody dtoUsuario dto) {
        try {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(dto.getNombre());
            nuevoUsuario.setApellidoPaterno(dto.getApellidoPaterno());
            nuevoUsuario.setApellidoMaterno(dto.getApellidoMaterno());
            nuevoUsuario.setEmail(dto.getEmail());
            nuevoUsuario.setContraseña(dto.getContraseña());
            nuevoUsuario.setEstado(dto.getEstado());

            // Cambiamos 'guardar' por 'crearUsuario'
            usuarioService.crearUsuario(nuevoUsuario, dto.getRolesIds());

            return ResponseEntity.ok(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // ACTUALIZAR (EDITAR)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Integer id, @RequestBody dtoUsuario dto) {
        try {
            // Mapeamos los datos del DTO a un objeto temporal
            Usuario usuarioDatos = new Usuario();
            usuarioDatos.setIdUsuario(id);
            usuarioDatos.setNombre(dto.getNombre());
            usuarioDatos.setApellidoPaterno(dto.getApellidoPaterno());
            usuarioDatos.setApellidoMaterno(dto.getApellidoMaterno());
            usuarioDatos.setEmail(dto.getEmail());
            usuarioDatos.setContraseña(dto.getContraseña());
            usuarioDatos.setEstado(dto.getEstado());

            // Llamamos al nuevo método especializado en actualización
            usuarioService.actualizarUsuario(usuarioDatos, dto.getRolesIds());

            return ResponseEntity.ok("{\"message\": \"Usuario actualizado correctamente.\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Integer id) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }

    @GetMapping("/perfil")
    public ResponseEntity<Usuario> obtenerPerfil(Authentication authentication) {
        String codigUsuario = authentication.getName();
        return repositoryUsuario.findByCodigUsuario(codigUsuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer id) {
        try {
            usuarioService.eliminarLogico(id);
            return ResponseEntity.ok("{\"message\": \"Usuario desactivado correctamente.\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}