package asistenciaescolar.asistenciaescolar.Dto;

import asistenciaescolar.asistenciaescolar.Model.Grado;
import asistenciaescolar.asistenciaescolar.Model.Seccion;
import asistenciaescolar.asistenciaescolar.Model.Turno;
import lombok.Data;

import java.time.LocalDate;
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
    private Integer idSeccion;
    private Seccion seccion;
    private Integer idGrado;
    private Grado grado;
    private String dni;
    private LocalDate fechaNaci;
    private Integer idTurno;
    private Turno turno;
    private  String codigoHash;

    private List<Integer> idsApoderados;
    private List<dtoApoderado> apoderadosNuevos;
    private List<dtoApoderado> apoderadosAsignados;
}
