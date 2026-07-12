package asistenciaescolar.asistenciaescolar.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class dtoReporteUsuario {
    private LocalDate fecha;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horaEntrada;
    private String alumnoNombreCompleto;
    private String gradoSeccion;
    private String estado;
    private String justificacion;
}