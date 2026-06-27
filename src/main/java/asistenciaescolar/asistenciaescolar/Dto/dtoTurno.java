package asistenciaescolar.asistenciaescolar.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalTime;

@Data
public class dtoTurno {
    private String turno;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horaEntrada;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horaEntradaLimite;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horaFaltaLimite;
}
