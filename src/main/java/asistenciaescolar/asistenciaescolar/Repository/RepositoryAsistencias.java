package asistenciaescolar.asistenciaescolar.Repository;

import asistenciaescolar.asistenciaescolar.Model.Alumno;
import asistenciaescolar.asistenciaescolar.Model.Asistencias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RepositoryAsistencias extends JpaRepository<Asistencias,Integer> {
    boolean existsByAlumnoAndFecha(Alumno alumno, LocalDate fecha);

}
