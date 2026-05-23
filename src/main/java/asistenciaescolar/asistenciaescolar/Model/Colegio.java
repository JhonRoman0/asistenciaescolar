package asistenciaescolar.asistenciaescolar.Model;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "Colegio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Colegio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idColegio;
    @Column(nullable = false, length = 100)
    private String colegio;
    @Column(nullable = false)
    private Short codigo;
    @Column(nullable = false, length = 100)
    private String direccion;
    private String telefono;
    @Column(nullable = false)
    private Short celular;
    @Column(nullable = false, length = 50)
    private String gmail;
}
