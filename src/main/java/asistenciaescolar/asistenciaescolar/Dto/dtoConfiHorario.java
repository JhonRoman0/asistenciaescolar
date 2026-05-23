package asistenciaescolar.asistenciaescolar.Dto;


import lombok.Data;

import java.time.LocalTime;

@Data
public class dtoConfiHorario {
    private LocalTime horaEntradaLimite;
    private LocalTime horaFaltaLimite;
    private dtoTurno turno;
}
