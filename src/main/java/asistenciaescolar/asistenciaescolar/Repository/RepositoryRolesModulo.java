package asistenciaescolar.asistenciaescolar.Repository;

import asistenciaescolar.asistenciaescolar.Model.RolesModulo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryRolesModulo extends JpaRepository<RolesModulo,Integer> {
    // Para listar los módulos de un Rol específico
    List<RolesModulo> findByRolIdRoles(Integer idRoles);

    // Para limpiar los accesos antiguos de un rol antes de asignarle los nuevos
    @Modifying
    @Transactional
    void deleteByRolIdRoles(Integer idRoles);
}
