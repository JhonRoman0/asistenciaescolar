package asistenciaescolar.asistenciaescolar.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "AlumnoApoderado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoApoderado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idAlumno", nullable = false)
    @JsonIgnoreProperties("alumnoApoderados")
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name = "idApoderado", nullable = false)
    private Apoderado apoderado;

    @Column(name = "es_principal", nullable = false)
    private Boolean esPrincipal = false;

    public AlumnoApoderado(Alumno alumno, Apoderado apoderado) {
        this.alumno = alumno;
        this.apoderado = apoderado;
        this.esPrincipal = false; // Por defecto inicia en false hasta que el service lo cambie
    }
}
