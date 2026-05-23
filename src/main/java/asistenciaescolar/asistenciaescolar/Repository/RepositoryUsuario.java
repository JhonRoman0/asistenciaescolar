package asistenciaescolar.asistenciaescolar.Repository;

import asistenciaescolar.asistenciaescolar.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryUsuario extends JpaRepository<Usuario,Integer> {
    boolean existsByEmail(String email);


    Optional<Usuario> findByCodigUsuario(String codigUsuario);
}
