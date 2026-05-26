package asistenciaescolar.asistenciaescolar.Repository;

import asistenciaescolar.asistenciaescolar.Model.Alumno;
import asistenciaescolar.asistenciaescolar.Model.AlumnoApoderado;
import asistenciaescolar.asistenciaescolar.Model.Apoderado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryAlumnoApoderado extends JpaRepository<AlumnoApoderado,Integer> {
    boolean existsByAlumnoAndApoderado(Alumno alumno, Apoderado apoderado);
}
