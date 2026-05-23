package asistenciaescolar.asistenciaescolar.Dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class dtoRoles {
    private String nombreRol;
    private Short estado;
    private LocalDate fechaCreacion;
    private String color;
}
