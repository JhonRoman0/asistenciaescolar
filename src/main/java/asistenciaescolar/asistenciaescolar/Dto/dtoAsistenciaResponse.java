package asistenciaescolar.asistenciaescolar.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class dtoAsistenciaResponse {
    private String nombreAlumno;
    private String apellidoPaterno;
    private String rutaFoto;
    private String estado;
    private String horaRegistro;
}
