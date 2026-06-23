package asistenciaescolar.asistenciaescolar.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Modulo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Modulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idModulo;
    @Column(nullable = false, length = 30)
    private String nombre;
    @Column(nullable = false)
    private Short estado;
    @Column(nullable = false)
    private LocalDate fechaCreacion;
}
