package asistenciaescolar.asistenciaescolar.Repository;

import asistenciaescolar.asistenciaescolar.Model.GradoSeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryGradoSeccion extends JpaRepository<GradoSeccion,Integer> {
}
