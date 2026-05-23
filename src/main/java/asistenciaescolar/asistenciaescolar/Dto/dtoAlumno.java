package asistenciaescolar.asistenciaescolar.Dto;

import lombok.Data;

@Data
public class dtoAlumno {
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String codigoUnico;
    private String rutaFoto;
    private Integer estado;
    private Integer idSeccion; // Solo el ID
    private Integer idGrado;   // Solo el ID
    private Integer dni;
}
