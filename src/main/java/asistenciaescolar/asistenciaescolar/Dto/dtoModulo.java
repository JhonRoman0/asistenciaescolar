package asistenciaescolar.asistenciaescolar.Dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class dtoModulo {
    private String nombre;
    private Short estado;
    private LocalDate fechaCreacion;
}
