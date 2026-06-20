package asistenciaescolar.asistenciaescolar.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;
    @Column(nullable = false, length = 20)
    private String nombre;
    @Column(nullable = false, length = 20)
    private String apellidoPaterno;
    @Column(nullable = false, length = 20)
    private String apellidoMaterno;
    @Column(nullable = false, length = 50)
    private String email;
    @Column(nullable = false)
    private String contraseña;
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    @Column(nullable = false)
    private Short estado;
    @Column(nullable = false)
    private String codigUsuario;
    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<UsuarioRoles> usuarioRoles;

}
