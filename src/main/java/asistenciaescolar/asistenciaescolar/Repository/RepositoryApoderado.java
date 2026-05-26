package asistenciaescolar.asistenciaescolar.Repository;

import asistenciaescolar.asistenciaescolar.Model.Apoderado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryApoderado extends JpaRepository<Apoderado,Integer> {
    // Ahora validamos y buscamos por el número de documento
    boolean existsByDni(String dni);

    Optional<Apoderado> findByDni(String dni);
}
