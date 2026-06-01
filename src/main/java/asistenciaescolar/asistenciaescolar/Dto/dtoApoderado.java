package asistenciaescolar.asistenciaescolar.Dto;

import lombok.Data;

@Data
public class dtoApoderado {
    private String dni;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String celular;
    private String email;
    private Integer idApoderado;
    private Boolean esPrincipal;
}
