package asistenciaescolar.asistenciaescolar.Dto;

import lombok.Data;

@Data
public class DniResponse {
    // Si quieres que esta clase sea la principal, mapea el campo 'data'
    private boolean success;
    private DniData data;
}