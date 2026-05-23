package asistenciaescolar.asistenciaescolar.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Apoderado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Apoderado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idApoderado;
    @Column(nullable = false, length = 20)
    private String nombre;
    @Column(nullable = false, length = 20)
    private String apellidoPaterno;
    @Column(nullable = false, length = 20)
    private String apellidoMaterno;
    @Column(nullable = false)
    private Short celular;
    @Column(nullable = false, length = 50)
    private String email;
}
