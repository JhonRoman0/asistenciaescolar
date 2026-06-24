/*
package asistenciaescolar.asistenciaescolar.Security;

import asistenciaescolar.asistenciaescolar.Model.Modulo;
import asistenciaescolar.asistenciaescolar.Model.Roles;
import asistenciaescolar.asistenciaescolar.Model.Usuario;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryUsuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private RepositoryUsuario repositoryUsuario;


    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. Obtener el código de usuario del login
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String codigoUsuario = userDetails.getUsername();

        // 2. Buscar el usuario directo con sus relaciones (EAGER)
        Usuario usuario = repositoryUsuario.findByCodigUsuario(codigoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Mapeamos los roles y módulos usando HashMaps directamente aquí para romper el ciclo
        List<Map<String, Object>> rolesList = usuario.getUsuarioRoles().stream()
                .map(usuarioRol -> {
                    Roles rol = usuarioRol.getRol();

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

        Map<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("idUsuario", usuario.getIdUsuario());
        usuarioMap.put("nombre", usuario.getNombre());
        usuarioMap.put("apellidoPaterno", usuario.getApellidoPaterno());
        usuarioMap.put("apellidoMaterno", usuario.getApellidoMaterno());
        usuarioMap.put("email", usuario.getEmail());
        usuarioMap.put("codigUsuario", usuario.getCodigUsuario());
        usuarioMap.put("roles", rolesList);

        // 4. Guardar en la sesión HTTP
        HttpSession session = request.getSession();
        session.setAttribute("PERFIL_USUARIO", usuarioMap);

        // 5. Redirigir al dashboard
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}
 */
