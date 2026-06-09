package asistenciaescolar.asistenciaescolar.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Justificacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Justificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idJustificacion;
    @Column(nullable = false, length = 100)
    private String descripcion;
}
