package asistenciaescolar.asistenciaescolar.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "Turno")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Turno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTurno;
    @Column(nullable = false, length = 20)
    private String turno;
    @Column(nullable = false)
    private LocalTime horaEntrada;
    @Column(nullable = false)
    private LocalTime horaEntradaLimite;
    @Column(nullable = false)
    private  LocalTime horaFaltaLimite;
}
