package asistenciaescolar.asistenciaescolar.Dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class dtoTurno {
    private String turno;
    private LocalTime horaEntrada;
    private LocalTime horaEntradaLimite;
    private LocalTime horaFaltaLimite;
}
