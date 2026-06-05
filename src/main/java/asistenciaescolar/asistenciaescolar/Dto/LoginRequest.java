package asistenciaescolar.asistenciaescolar.Dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String codigUsuario;
    private String contrasenia;
}
