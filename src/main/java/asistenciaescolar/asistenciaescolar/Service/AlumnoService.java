package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Dto.dtoAlumno;
import asistenciaescolar.asistenciaescolar.Model.Alumno;
import asistenciaescolar.asistenciaescolar.Model.Grado;
import asistenciaescolar.asistenciaescolar.Model.Seccion;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryAlumno;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryGrado;
import asistenciaescolar.asistenciaescolar.Repository.RepositorySeccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class AlumnoService {

    @Autowired
    private RepositoryAlumno alumnoRepository;

    @Autowired
    private RepositoryGrado gradoRepository;

    @Autowired
    private RepositorySeccion seccionRepository;

    // 1. Método para REGISTRAR
    public Alumno guardarAlumno(dtoAlumno dto) {
        // Validamos que el código único no exista para evitar errores de duplicidad
        if (alumnoRepository.existsByDni(dto.getDni())) {
            throw new RuntimeException("El DNI " + dto.getDni() + " ya está registrado en el sistema.");
        }
        Alumno alumno = new Alumno();
        return mapearYGuardar(alumno, dto);
    }

    public Alumno obtenerPorId(Integer id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
    }

    // 2. Método para ACTUALIZAR
    public Alumno actualizarAlumno(Integer id, dtoAlumno dto) {
        Alumno alumnoExistente = alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id));
        if (!alumnoExistente.getDni().equals(dto.getDni()) && alumnoRepository.existsByDni(dto.getDni())) {
            throw new RuntimeException("El DNI " + dto.getDni() + " ya pertenece a otro alumno.");
        }

        return mapearYGuardar(alumnoExistente, dto);
    }

    // 3. Método para LISTAR CON FILTROS (El que pide tu tabla)
    public List<Alumno> listarConFiltros(String nombre, Integer idGrado, Integer idSeccion, Integer estado) {
        // Limpiamos el nombre para la búsqueda LIKE
        String nombreBusqueda = (nombre != null && !nombre.trim().isEmpty()) ? nombre : null;

        return alumnoRepository.buscarConFiltros(nombreBusqueda, idGrado, idSeccion, estado);
    }
    private String generarCodigoUnico() {
        String nuevoCodigo;
        boolean existe;
        do {
            // Ejemplo: ALU- seguido de 6 números aleatorios
            int numero = (int)(Math.random() * 900000) + 100000;
            nuevoCodigo = "ALU" + numero;

            // Verificamos en la BD que no exista ya ese código
            existe = alumnoRepository.existsByCodigoUnico(nuevoCodigo);
        } while (existe);

        return nuevoCodigo;
    }

    // 4. Lógica de mapeo común para evitar repetir código
    private Alumno mapearYGuardar(Alumno alumno, dtoAlumno dto) {
        alumno.setNombre(dto.getNombre());
        alumno.setApellidoPaterno(dto.getApellidoPaterno());
        alumno.setApellidoMaterno(dto.getApellidoMaterno());
        alumno.setCodigoUnico(dto.getCodigoUnico());
        alumno.setRutaFoto(dto.getRutaFoto());
        alumno.setDni(dto.getDni());

        // SOLUCIÓN AL ERROR DE RUTA FOTO:
        // Si el DTO no trae ruta, ponemos una por defecto para que la BD no salte
        if (dto.getRutaFoto() == null || dto.getRutaFoto().isEmpty()) {
            alumno.setRutaFoto("default-profile.png"); // Valor temporal
        } else {
            alumno.setRutaFoto(dto.getRutaFoto());
        }

        if (alumno.getIdAlumno() == null) {
            alumno.setCodigoUnico(generarCodigoUnico());
        }

        // El estado lo recibes como 0, 1 o 2
        alumno.setEstado(dto.getEstado() != null ? dto.getEstado() : 1); // 1 (Activo) por defecto

        // Buscamos las entidades relacionadas
        Grado grado = gradoRepository.findById(dto.getIdGrado())
                .orElseThrow(() -> new RuntimeException("Grado no encontrado"));
        Seccion seccion = seccionRepository.findById(dto.getIdSeccion())
                .orElseThrow(() -> new RuntimeException("Sección no encontrada"));

        alumno.setGrado(grado);
        alumno.setSeccion(seccion);

        return alumnoRepository.save(alumno);
    }

    public List<Grado> listarGrados() {
        return gradoRepository.findAll(); // Trae todos los grados de la BD
    }

    public List<Seccion> listarSecciones() {
        return seccionRepository.findAll(); // Trae todas las secciones de la BD
    }

    public void eliminarLogico(Integer id) {
        // 1. Buscamos al alumno
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el alumno con ID: " + id));

        // 2. Cambiamos solo el estado a 2
        alumno.setEstado(2);

        // 3. Guardamos los cambios
        alumnoRepository.save(alumno);
    }
}
