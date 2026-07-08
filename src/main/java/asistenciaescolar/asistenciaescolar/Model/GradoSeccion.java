package asistenciaescolar.asistenciaescolar.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Grado_Seccion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GradoSeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGradoSeccion;

    @ManyToOne
    @JoinColumn(name = "idGrado", nullable = false)
    private Grado grado;

    @ManyToOne
    @JoinColumn(name = "idSeccion", nullable = false)
    private Seccion seccion;
}