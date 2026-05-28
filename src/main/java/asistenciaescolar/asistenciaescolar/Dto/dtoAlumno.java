package asistenciaescolar.asistenciaescolar.Dto;

import lombok.Data;

import java.util.List;

@Data
public class dtoAlumno {
    private Integer idAlumno;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String codigoUnico;
    private String rutaFoto;
    private Integer estado;
    private Integer idSeccion; // Solo el ID
    private Integer idGrado;   // Solo el ID
    private String dni;

    // AMBAS OPCIONES EN EL DTO:
    private List<dtoApoderado> idsApoderados;   // Opción A: Mandan solo el ID si ya existe
    private List<dtoApoderado> apoderadosNuevos; // Opción B: Mandan los datos completos si es nuevo
    private List<dtoApoderado> apoderadosAsignados; // Aquí viajan los datos completos al Frontend
}
