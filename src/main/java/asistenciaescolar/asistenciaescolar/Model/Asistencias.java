package asistenciaescolar.asistenciaescolar.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "Asistencias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Asistencias {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAsistencias;
    @Column(nullable = false)
    private LocalDate fecha;
    @Column(nullable = false)
    private LocalTime horaEntrada;

    @ManyToOne
    @JoinColumn(name = "idAlumno",nullable = false)
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name = "idEstado",nullable = false)
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "idJustificacion",nullable = true)
    private Justificacion justificacion;

    @ManyToOne
    @JoinColumn(name = "idUsuarioRegistro", nullable = false)
    private Usuario usuarioRegistro;
}