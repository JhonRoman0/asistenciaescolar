package asistenciaescolar.asistenciaescolar.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DniData {
    @JsonProperty("nombres")
    private String nombres;

    @JsonProperty("apellido_paterno")
    private String apellidoPaterno;

    @JsonProperty("apellido_materno")
    private String apellidoMaterno;


    @JsonProperty("numero")
    private String dni;
}