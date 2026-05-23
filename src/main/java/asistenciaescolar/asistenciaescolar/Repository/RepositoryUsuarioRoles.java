package asistenciaescolar.asistenciaescolar.Repository;

import asistenciaescolar.asistenciaescolar.Model.Usuario;
import asistenciaescolar.asistenciaescolar.Model.UsuarioRoles;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryUsuarioRoles extends JpaRepository<UsuarioRoles,Integer> {
    @Modifying
    @Transactional
    void deleteByUsuario(Usuario usuario);
}
