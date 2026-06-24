package asistenciaescolar.asistenciaescolar.Dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class dtoRolesResponse {
    private Integer idRoles;
    private String nombreRol;
    private Short estado;
    private LocalDate fechaCreacion;
    private String color;
    private List<Integer> idModulos;
    private Long cantidadUsuariosActivos; // El nuevo campo para el Front-end
}