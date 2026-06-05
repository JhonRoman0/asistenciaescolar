package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Dto.LoginRequest;
import asistenciaescolar.asistenciaescolar.Model.Modulo;
import asistenciaescolar.asistenciaescolar.Model.Roles;
import asistenciaescolar.asistenciaescolar.Model.Usuario;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryUsuario;
import asistenciaescolar.asistenciaescolar.Security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:3000") // Crucial para que Next.js pueda conectarse sin bloqueos de CORS
public class RestAuthController {

    @Autowired
    private CustomUserDetailsService userDetailsService;// Agregado: Necesario para cargar el UserDetails


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RepositoryUsuario repositoryUsuario;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Cargar el usuario desde tu CustomUserDetailsService
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getCodigUsuario());

            // 2. Validar la contraseña usando el encriptador
            if (!passwordEncoder.matches(loginRequest.getContrasenia(), userDetails.getPassword())) {
                return ResponseEntity.status(401).body(Map.of("error", "Contraseña incorrecta"));
            }

            // 3. Autenticar al usuario en el contexto de Spring Security
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 4. Buscar los datos completos para armar la respuesta libre de recursión infinita
            Usuario usuario = repositoryUsuario.findByCodigUsuario(loginRequest.getCodigUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // 5. Mapeo estructurado de Roles y Módulos Activos
            List<Map<String, Object>> rolesList = usuario.getUsuarioRoles().stream()
                    .filter(ur -> ur.getRol().getEstado() == 1) // Solo roles activos
                    .map(usuarioRol -> {
                        Roles rol = usuarioRol.getRol();

                        // Extraer módulos del rol
                        List<Map<String, Object>> modulosList = rol.getRolesModulos().stream()
                                .map(rolModulo -> {
                                    Modulo modulo = rolModulo.getModulo();
                                    Map<String, Object> moduloMap = new HashMap<>();
                                    moduloMap.put("idModulo", modulo.getIdModulo());
                                    moduloMap.put("nombre", modulo.getNombre());
                                    return moduloMap;
                                })
                                .collect(Collectors.toList());

                        Map<String, Object> rolMap = new HashMap<>();
                        rolMap.put("idRoles", rol.getIdRoles());
                        rolMap.put("nombreRol", rol.getNombreRol());
                        rolMap.put("color", rol.getColor());
                        rolMap.put("modulos", modulosList);
                        return rolMap;
                    })
                    .collect(Collectors.toList());

            // 6. Construir el objeto JSON final del perfil
            Map<String, Object> respuestaPerfil = new HashMap<>();
            respuestaPerfil.put("idUsuario", usuario.getIdUsuario());
            respuestaPerfil.put("nombre", usuario.getNombre());
            respuestaPerfil.put("apellidoPaterno", usuario.getApellidoPaterno());
            respuestaPerfil.put("apellidoMaterno", usuario.getApellidoMaterno());
            respuestaPerfil.put("email", usuario.getEmail());
            respuestaPerfil.put("codigUsuario", usuario.getCodigUsuario());
            respuestaPerfil.put("roles", rolesList);

            // 7. Retornar los datos al front con estado 200 OK
            return ResponseEntity.ok(respuestaPerfil);

        } catch (Exception e) {
            // Retorna 401 si el usuario no existe o está inactivo
            return ResponseEntity.status(401).body(Map.of("error", "Usuario no encontrado o credenciales inválidas"));
        }
    }
}