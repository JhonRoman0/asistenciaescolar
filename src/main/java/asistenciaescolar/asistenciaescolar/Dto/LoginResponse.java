package asistenciaescolar.asistenciaescolar.Dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class LoginResponse {
    private Integer idUsuario;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String email;
    private String codigUsuario;
    private List<RolInfo> roles;

    @Getter
    @Builder
    public static class RolInfo {
        private Integer idRoles;
        private String nombreRol;
        private String color;
        private List<ModuloInfo> modulos;
    }

    @Getter
    @Builder
    public static class ModuloInfo {
        private Integer idModulo;
        private String nombre;
    }
}