package asistenciaescolar.asistenciaescolar.Dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class dtoUsuarioRoles {
    private dtoUsuario usuario;
    private dtoRoles rol;
    private LocalDate fechaAsignacion;
}
