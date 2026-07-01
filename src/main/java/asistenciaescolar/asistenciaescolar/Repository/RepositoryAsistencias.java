package asistenciaescolar.asistenciaescolar.Repository;

import asistenciaescolar.asistenciaescolar.Model.Alumno;
import asistenciaescolar.asistenciaescolar.Model.Asistencias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryAsistencias extends JpaRepository<Asistencias,Integer> {
    boolean existsByAlumnoAndFecha(Alumno alumno, LocalDate fecha);

    // Para el endpoint /hoy y la matriz semanal
    Optional<Asistencias> findByAlumnoAndFecha(Alumno alumno, LocalDate fecha);

    // Para las estadísticas: contar cuántos registros hay en una fecha por estado
    long countByFechaAndEstado_IdEstado(LocalDate fecha, Integer idEstado);

    // Para el reporte mensual/semanal
    List<Asistencias> findByFechaBetween(LocalDate inicio, LocalDate fin);

}
