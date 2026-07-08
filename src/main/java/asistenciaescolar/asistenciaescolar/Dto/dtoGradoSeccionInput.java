package asistenciaescolar.asistenciaescolar.Dto;

import lombok.Data;
import java.util.List;

@Data
public class dtoGradoSeccionInput {
    private String grado;            // Ej: "1 PRIMARIA"
    private List<String> secciones;   // Ej: ["A", "B"] (Específicas para este grado)
}