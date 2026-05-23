package asistenciaescolar.asistenciaescolar.Model;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "Estado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEstado;
    @Column(nullable = false, length = 10)
    private String estado;
}
