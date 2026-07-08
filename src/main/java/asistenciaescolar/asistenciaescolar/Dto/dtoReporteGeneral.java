package asistenciaescolar.asistenciaescolar.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class dtoReporteGeneral {
    private LocalDate fecha;
    private LocalTime horaEntrada;
    private String codigoUnico;
    private String alumnoNombreCompleto;
    private String gradoSeccion;
    private String estado;
    private String justificacion;
    private String usuarioRegistrador;
}