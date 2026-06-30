package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Dto.LoginRequest;
import asistenciaescolar.asistenciaescolar.Dto.LoginResponse;
import asistenciaescolar.asistenciaescolar.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoint para el inicio de sesión del sistema")
public class RestAuthController {

    private final AuthService authService;

    // 👇 1. DEFINIMOS EL REPOSITORIO OFICIAL DE CONTEXTO
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión en el sistema")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response // 👈 2. INYECTAMOS EL RESPONSE AQUÍ
    ) {
        LoginResponse respuesta = authService.autenticar(loginRequest);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // 👇 3. ESTO OBLIGA A SPRING SECURITY 6 A SETEAR LA COOKIE EMITIENDO EL HEADER
            securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
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