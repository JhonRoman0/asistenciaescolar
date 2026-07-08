package asistenciaescolar.asistenciaescolar.Dto;

import lombok.Data;

import java.util.List;

@Data
public class dtoColegio {
    private String colegio;
    private String codigo;
    private String direccion;
    private String telefono;
    private String celular;
    private String gmail;
    private List<dtoTurno> turnos;
    private List<dtoGradoSeccionInput> configuracionGrados;
}
