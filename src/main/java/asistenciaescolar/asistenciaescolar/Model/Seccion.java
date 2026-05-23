package asistenciaescolar.asistenciaescolar.Model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "Seccion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSeccion;
    @Column(nullable = false, length = 20)
    private String seccion;

}
