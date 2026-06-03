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
                "(:nombre IS NULL OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
                "(:idGrado IS NULL OR a.grado.idGrado = :idGrado) AND " +
                "(:idSeccion IS NULL OR a.seccion.idSeccion = :idSeccion) AND " +
                "(:estado IS NULL OR a.estado = :estado)")
        List<Alumno> buscarConFiltros(
                @Param("nombre") String nombre,
                @Param("idGrado") Integer idGrado,
                @Param("idSeccion") Integer idSeccion,
                @Param("estado") Integer estado
        );

        boolean existsByCodigoUnico(String codigoUnico);
        boolean existsByDni(String dni);

}

