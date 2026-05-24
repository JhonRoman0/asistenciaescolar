package asistenciaescolar.asistenciaescolar.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "RolesModulo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolesModulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idRoles",nullable = false)
    @JsonIgnore
    private Roles rol;

    @ManyToOne
    @JoinColumn(name = "idModulo",nullable = false)
    private Modulo modulo;
}
