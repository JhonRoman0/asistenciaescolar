package asistenciaescolar.asistenciaescolar.Repository;

import asistenciaescolar.asistenciaescolar.Model.Alumno;
import asistenciaescolar.asistenciaescolar.Model.AlumnoApoderado;
import asistenciaescolar.asistenciaescolar.Model.Apoderado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryAlumnoApoderado extends JpaRepository<AlumnoApoderado,Integer> {
    boolean existsByAlumnoAndApoderado(Alumno alumno, Apoderado apoderado);
    Optional<AlumnoApoderado> findByAlumnoAndApoderado(Alumno alumno, Apoderado apoderado);

    List<AlumnoApoderado> findByAlumno(Alumno alumno);
}
