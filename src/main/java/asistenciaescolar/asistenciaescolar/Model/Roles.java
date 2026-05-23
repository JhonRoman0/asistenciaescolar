package asistenciaescolar.asistenciaescolar.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "Roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRoles;
    @Column(nullable = false, length = 20)
    private String nombreRol;
    @Column(nullable = false)
    private Short estado;
    @Column(nullable = false)
    private LocalDate fechaCreacion;
    @Column(nullable = false,length = 7)
    private String Color="#4361EE";
}
