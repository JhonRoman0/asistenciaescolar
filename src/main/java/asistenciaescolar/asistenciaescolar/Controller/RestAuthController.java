package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Dto.LoginRequest;
import asistenciaescolar.asistenciaescolar.Dto.LoginResponse;
import asistenciaescolar.asistenciaescolar.Model.Modulo;
import asistenciaescolar.asistenciaescolar.Model.Roles;
import asistenciaescolar.asistenciaescolar.Model.Usuario;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryUsuario;
import asistenciaescolar.asistenciaescolar.Security.CustomUserDetailsService;
import asistenciaescolar.asistenciaescolar.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoint para el inicio de sesión del sistema")
public class RestAuthController {

    private final AuthService authService;

    // Reemplaza el método login actual por este en RestAuthController.java

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión en el sistema")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request // <--- Inyectamos el request de Jakarta
    ) {
        // 1. Ejecuta la lógica actual del servicio (valida contraseña y carga datos)
        LoginResponse respuesta = authService.autenticar(loginRequest);
        // 2. Cargamos el usuario autenticado que el AuthService acaba de meter al Contexto
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // 3. Forzamos la creación/obtención de la sesión HTTP en el servidor
            var session = request.getSession(true);
            // 4. Guardamos el contexto de seguridad dentro de la sesión para que persista entre peticiones
            session.setAttribute(
                    org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );
        }
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/perfil-sesion")
    @Operation(summary = "Obtener el perfil del usuario autenticado actualmente")
    public ResponseEntity<LoginResponse> obtenerPerfilSesion() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal().toString())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No hay una sesión activa");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Reutilizamos la lógica del servicio para armar el DTO completo con roles y módulos
        LoginRequest requestSimulado = new LoginRequest();
        requestSimulado.setCodigUsuario(userDetails.getUsername());

        // Creamos un método alternativo rápido en el service o buscamos directamente por código
        LoginResponse respuesta = authService.obtenerRespuestaPorCodigo(userDetails.getUsername());
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión e invalidar la cookie")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return ResponseEntity.noContent().build();
    }
}