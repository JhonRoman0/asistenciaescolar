package asistenciaescolar.asistenciaescolar.Dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class dtoAsistencias {
    private LocalDate fecha;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horaEntrada;
    private dtoAlumno alumno;
    private dtoEstado estado;
    private dtoTurno turno;
    private dtoJustificacion justificacion;
}
