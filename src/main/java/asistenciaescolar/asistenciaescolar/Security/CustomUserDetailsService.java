package asistenciaescolar.asistenciaescolar.Security;

import asistenciaescolar.asistenciaescolar.Model.Usuario;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private RepositoryUsuario repositoryUsuario;
    @Override
    public UserDetails loadUserByUsername(String codigUsuario) throws UsernameNotFoundException{
        Usuario usuario = repositoryUsuario.findByCodigUsuario(codigUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con codigo usuario: " + codigUsuario));

        List<GrantedAuthority> authorities = usuario.getUsuarioRoles().stream()
                .filter(ur -> ur.getRol().getEstado() == 1)
                // IMPORTANTE: Concatenamos "ROLE_" al nombre del rol
                .map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRol().getNombreRol()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                usuario.getCodigUsuario(),
                usuario.getContraseña(),
                usuario.getEstado()==1,
                true,true,true,
                authorities
        );
    }

}
