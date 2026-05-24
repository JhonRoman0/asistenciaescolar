package asistenciaescolar.asistenciaescolar.Dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class dtoRoles {
    private String nombreRol;
    private Short estado;
    private LocalDate fechaCreacion;
    private String color="#4361EE";
    private List<Integer> idModulos;
}
