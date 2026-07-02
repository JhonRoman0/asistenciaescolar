package asistenciaescolar.asistenciaescolar.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class dtoAsistenciaResponse {
    private String nombreAlumno;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String rutaFoto;
    private String codigoUnico;     // NUEVO
    private String grado;           // NUEVO
    private String seccion;         // NUEVO
    private String turno;
    private String estado;
    private String horaRegistro;
}
