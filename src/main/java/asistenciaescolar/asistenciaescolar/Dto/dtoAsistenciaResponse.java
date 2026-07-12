package asistenciaescolar.asistenciaescolar.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String codigoUnico;
    private Integer idGradoSeccion;
    private String turno;
    private String estado;
    @JsonFormat(pattern = "HH:mm:ss")
    private String horaRegistro;
}
