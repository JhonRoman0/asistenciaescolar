package asistenciaescolar.asistenciaescolar.Repository;

import asistenciaescolar.asistenciaescolar.Model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryAlumno extends JpaRepository<Alumno,Integer> {

        // 2. Filtros dinámicos para la tabla (El que causaba el error)
        @Query("SELECT a FROM Alumno a WHERE " +
                "(:nombre IS NULL OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) OR LOWER(a.apellidoPaterno) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
                "(:idGradoSeccion IS NULL OR a.gradoSeccion.idGradoSeccion = :idGradoSeccion) AND " +
                "(:estado IS NULL OR a.estado = :estado)")
        List<Alumno> buscarConFiltros(
                @Param("nombre") String nombre,
                @Param("idGradoSeccion") Integer idGradoSeccion,
                @Param("estado") Integer estado
        );

        boolean existsByCodigoUnico(String codigoUnico);
        boolean existsByDni(String dni);
    Optional<Alumno> findByDni(String dni);

        Optional<Alumno> findByCodigoHash(String codigoHash);
        Optional<Alumno> findByCodigoUnico(String codigoUnico);
    // Este es clave para el endpoint "hoy": listar alumnos activos (estado = 1)
    List<Alumno> findByEstado(Integer estado);

}

