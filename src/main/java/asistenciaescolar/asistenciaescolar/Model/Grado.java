package asistenciaescolar.asistenciaescolar.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Grado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Grado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGrado;
    @Column(name = "grado", length = 50, nullable = false)
    private  String grado;
}
