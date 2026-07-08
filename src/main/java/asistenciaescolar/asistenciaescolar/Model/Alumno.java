package asistenciaescolar.asistenciaescolar.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Alumno")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAlumno;
    @Column(nullable = false, length = 20)
    private String nombre;
    @Column(nullable = false, length = 20)
    private String apellidoMaterno;
    @Column(nullable = false, length = 20)
    private String apellidoPaterno;
    @Column(nullable = false, length = 500)
    private String rutaFoto;
    @Column(name = "foto_public_id", nullable = true)
    private String fotoPublicId;
    @Column(nullable = false)
    private String codigoUnico;
    @Column(nullable = false)
    private Integer estado;
    @Column(nullable = false)
    private String dni;
    @Column(nullable = false)
    private LocalDate fechaNaci;
    @Column(nullable = false)
    private  String codigoHash;

    @ManyToOne
    @JoinColumn(name = "idTurno",nullable = false)
    private Turno turno;


    @ManyToOne
    @JoinColumn(name = "idGradoSeccion", nullable = false)
    private GradoSeccion gradoSeccion;

    @OneToMany(mappedBy = "alumno", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("alumno")
    private List<AlumnoApoderado> alumnoApoderados;

}
