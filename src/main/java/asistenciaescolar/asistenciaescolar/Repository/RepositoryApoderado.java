package asistenciaescolar.asistenciaescolar.Repository;

import asistenciaescolar.asistenciaescolar.Model.Apoderado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryApoderado extends JpaRepository<Apoderado,Integer> {
    // Ahora validamos y buscamos por el número de documento
    boolean existsByDni(String dni);

    Optional<Apoderado> findByDni(String dni);

    // Solo busca si el DNI coincide Y el apoderado está activo (estado = 1)
    Optional<Apoderado> findByDniAndEstado(String dni, Integer estado);

    // Solo lista los que están activos en el sistema
    List<Apoderado> findByEstado(Integer estado);

    boolean existsByDniAndEstado(String dni, Integer estado);
}
