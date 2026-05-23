package asistenciaescolar.asistenciaescolar.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "UsuarioRoles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idUsuario",nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idRoles",nullable = false)
    private Roles rol;

    private LocalDate fechaAsignacion;
}
