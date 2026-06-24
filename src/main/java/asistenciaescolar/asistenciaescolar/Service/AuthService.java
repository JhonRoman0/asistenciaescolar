package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Dto.LoginRequest;
import asistenciaescolar.asistenciaescolar.Dto.LoginResponse;
import asistenciaescolar.asistenciaescolar.Model.Usuario;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryUsuario;
import asistenciaescolar.asistenciaescolar.Security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final RepositoryUsuario repositoryUsuario;

    @Transactional(readOnly = true)
    public LoginResponse autenticar(LoginRequest loginRequest) {
        UserDetails userDetails;

        try {
            // 1. Cargar el usuario
            userDetails = userDetailsService.loadUserByUsername(loginRequest.getCodigUsuario());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado o credenciales inválidas");
        }

        // 2. Validar la contraseña usando el encriptador
        if (!passwordEncoder.matches(loginRequest.getContrasenia(), userDetails.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado o credenciales inválidas");
        }

        // 3. Autenticar al usuario en el contexto de Spring Security
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 4. Buscar la entidad completa
        Usuario usuario = repositoryUsuario.findByCodigUsuario(loginRequest.getCodigUsuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        // 5. Construcción estructurada del DTO utilizando Builder de Lombok (libre de recursión)
        List<LoginResponse.RolInfo> roles = usuario.getUsuarioRoles().stream()
                .filter(ur -> ur.getRol().getEstado() == 1)
                .map(usuarioRol -> {
                    var rol = usuarioRol.getRol();

                    List<LoginResponse.ModuloInfo> modulos = rol.getRolesModulos().stream()
                            .map(rolModulo -> {
                                var modulo = rolModulo.getModulo();
                                return LoginResponse.ModuloInfo.builder()
                                        .idModulo(modulo.getIdModulo())
                                        .nombre(modulo.getNombre())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    return LoginResponse.RolInfo.builder()
                            .idRoles(rol.getIdRoles())
                            .nombreRol(rol.getNombreRol())
                            .color(rol.getColor())
                            .modulos(modulos)
                            .build();
                })
                .collect(Collectors.toList());

        return LoginResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .apellidoPaterno(usuario.getApellidoPaterno())
                .apellidoMaterno(usuario.getApellidoMaterno())
                .email(usuario.getEmail())
                .codigUsuario(usuario.getCodigUsuario())
                .roles(roles)
                .build();
    }

    @Transactional(readOnly = true)
    public LoginResponse obtenerRespuestaPorCodigo(String codigUsuario) {
        Usuario usuario = repositoryUsuario.findByCodigUsuario(codigUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        List<LoginResponse.RolInfo> roles = usuario.getUsuarioRoles().stream()
                .filter(ur -> ur.getRol().getEstado() == 1)
                .map(usuarioRol -> {
                    var rol = usuarioRol.getRol();
                    List<LoginResponse.ModuloInfo> modulos = rol.getRolesModulos().stream()
                            .map(rolModulo -> {
                                var modulo = rolModulo.getModulo();
                                return LoginResponse.ModuloInfo.builder()
                                        .idModulo(modulo.getIdModulo())
                                        .nombre(modulo.getNombre())
                                        .build();
                            }).collect(Collectors.toList());

                    return LoginResponse.RolInfo.builder()
                            .idRoles(rol.getIdRoles())
                            .nombreRol(rol.getNombreRol())
                            .color(rol.getColor())
                            .modulos(modulos)
                            .build();
                }).collect(Collectors.toList());

        return LoginResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .apellidoPaterno(usuario.getApellidoPaterno())
                .apellidoMaterno(usuario.getApellidoMaterno())
                .email(usuario.getEmail())
                .codigUsuario(usuario.getCodigUsuario())
                .roles(roles)
                .build();
    }
}