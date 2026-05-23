package asistenciaescolar.asistenciaescolar.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;


@Entity
@Table(name = "ConfiHorario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfiHorario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idConfiHorario;
    @Column(nullable = false)
    private LocalTime horaEntradaLimite;
    @Column(nullable = false)
    private LocalTime horaFaltaLimite;

    @ManyToOne
    @JoinColumn(name = "Turno_idTurno",nullable = false)
    private Turno turno;
}
