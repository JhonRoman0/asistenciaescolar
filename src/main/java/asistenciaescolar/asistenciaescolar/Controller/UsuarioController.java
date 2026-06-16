package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Model.Roles;
import asistenciaescolar.asistenciaescolar.Service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import asistenciaescolar.asistenciaescolar.Model.Usuario;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.Map;

import asistenciaescolar.asistenciaescolar.Dto.dtoUsuario;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // LISTAR TODOS
    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    // Obtener roles para llenar los checkboxes del HTML
    @GetMapping("/roles-disponibles")
    public ResponseEntity<List<Roles>> obtenerRolesParaRegistro() {
        return ResponseEntity.ok(usuarioService.obtenerRolesDisponibles());
    }

    // REGISTRO (CREAR) usando el DTO
    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody dtoUsuario dto) {
        Usuario nuevoUsuario = usuarioService.crearUsuarioDesdeDto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    // ACTUALIZAR (EDITAR)
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Integer id, @RequestBody dtoUsuario dto) {
        Usuario usuarioActualizado = usuarioService.actualizarUsuarioDesdeDto(id, dto);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Integer id) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/perfil")
    public ResponseEntity<Usuario> obtenerPerfil(Authentication authentication) {
        String codigUsuario = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorCodigoUsuario(codigUsuario);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {
        usuarioService.eliminarLogico(id);
        return ResponseEntity.noContent().build(); // Estándar HTTP 204 No Content
    }

    @GetMapping("/perfil-sesion")
    public ResponseEntity<Map<String, Object>> obtenerPerfilDesdeSesion(HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<String, Object> perfil = (Map<String, Object>) session.getAttribute("PERFIL_USUARIO");

        if (perfil == null) {
            return ResponseEntity.status(401).build(); // Si no hay sesión, responde No Autorizado
        }

        return ResponseEntity.ok(perfil);
    }
}