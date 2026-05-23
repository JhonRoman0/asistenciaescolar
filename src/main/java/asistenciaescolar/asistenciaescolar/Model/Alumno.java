package asistenciaescolar.asistenciaescolar.Model;

import jakarta.persistence.*;
import lombok.*;

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
    @Column(nullable = false)
    private String rutaFoto;
    @Column(nullable = false)
    private String codigoUnico;
    @Column(nullable = false)
    private Integer estado;
    @Column(nullable = false)
    private Integer dni;

    @ManyToOne
    @JoinColumn(name = "idSeccion", nullable = false)
    private Seccion seccion;

    @ManyToOne
    @JoinColumn(name = "idGrado", nullable = false)
    private Grado grado;

}
