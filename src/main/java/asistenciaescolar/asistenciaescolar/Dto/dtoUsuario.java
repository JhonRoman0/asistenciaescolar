package asistenciaescolar.asistenciaescolar.Dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class dtoUsuario {
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String email;
    private String contraseña;
    private LocalDateTime fechaCreacion;
    private Short estado;
    private String codigUsuario;
    private List<Integer> rolesIds;
}
