package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Dto.dtoAlumno;
import asistenciaescolar.asistenciaescolar.Dto.dtoApoderado;
import asistenciaescolar.asistenciaescolar.Model.*;
import asistenciaescolar.asistenciaescolar.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlumnoService {
    @Autowired
    private RepositoryApoderado apoderadoRepository;

    @Autowired
    private RepositoryAlumnoApoderado alumnoApoderadoRepository;

    @Autowired
    private RepositoryAlumno alumnoRepository;

    @Autowired
    private RepositoryGrado gradoRepository;

    @Autowired
    private RepositorySeccion seccionRepository;

    // 1. Método para REGISTRAR
    @Transactional
    public Alumno guardarAlumno(dtoAlumno dto) {
        // 1. Validamos que el DNI único no exista para evitar errores de duplicidad
        if (alumnoRepository.existsByDni(dto.getDni())) {
            throw new RuntimeException("El DNI " + dto.getDni() + " ya está registrado en el sistema.");
        }

        // 2. Guardar al Alumno primero
        Alumno alumno = new Alumno();
        Alumno alumnoGuardado = mapearYGuardar(alumno, dto);

        // 3. CONTROL INTELIGENTE DEL APODERADO POR DNI (¡Aquí llamamos al método modular!)
        procesarYAsociarApoderado(alumnoGuardado, dto);

        return alumnoGuardado;
    }

    public dtoAlumno obtenerPorId(Integer id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id));
        return convertirADto(alumno);
    }

    // 2. Método para ACTUALIZAR
    @Transactional
    public Alumno actualizarAlumno(Integer id, dtoAlumno dto) {
        Alumno alumnoExistente = alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id));
        if (!alumnoExistente.getDni().equals(dto.getDni()) && alumnoRepository.existsByDni(dto.getDni())) {
            throw new RuntimeException("El DNI " + dto.getDni() + " ya pertenece a otro alumno.");
        }
        // Actualizamos los datos propios del alumno
        Alumno alumnoActualizado = mapearYGuardar(alumnoExistente, dto);

        // Si el frontend envía datos de apoderado en la edición, procesamos la asignación
        procesarYAsociarApoderado(alumnoActualizado, dto);

        return alumnoActualizado;
    }

    // Este es el que ahora llama tu controlador en la línea 25
    public List<dtoAlumno> listarAlumnosConApoderados(String nombre, Integer idGrado, Integer idSeccion, Integer estado) {
        // 1. Aquí se ejecuta exactamente tu misma lógica de limpiar el nombre para el LIKE
        String nombreBusqueda = (nombre != null && !nombre.trim().isEmpty()) ? nombre : null;
        List<Alumno> alumnos = alumnoRepository.buscarConFiltros(nombreBusqueda, idGrado, idSeccion, estado);

        // 2. Convertimos esos alumnos filtrados a tu dtoAlumno (recolectando sus apoderados)
        return alumnos.stream().map(this::convertirADto).toList();
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

    // Método privado auxiliar para reutilizar la lógica del apoderado tanto en crear como en editar
    private void procesarYAsociarApoderado(Alumno alumno, dtoAlumno dto) {

        // =========================================================================
        // CASO C: (NUEVO) El frontend mandó los apoderados ya vinculados en la lista de 'apoderadosAsignados'
        // Sirve para actualizar directamente los datos de los padres actuales del alumno
        // =========================================================================
        if (dto.getApoderadosAsignados() != null && !dto.getApoderadosAsignados().isEmpty()) {
            for (dtoApoderado dtoAp : dto.getApoderadosAsignados()) {
                if (dtoAp.getIdApoderado() != null) {
                    // 1. Buscamos el apoderado existente por su ID
                    Apoderado apoderadoExistente = apoderadoRepository.findById(dtoAp.getIdApoderado())
                            .orElseThrow(() -> new RuntimeException("El apoderado con ID " + dtoAp.getIdApoderado() + " no existe."));

                    // 2. Validamos que si se cambió el DNI en el input, no choque con otra persona en la BD
                    if (dtoAp.getDni() != null) {
                        String dniApoderado = dtoAp.getDni().trim();
                        if (!apoderadoExistente.getDni().equals(dniApoderado) && apoderadoRepository.existsByDni(dniApoderado)) {
                            throw new RuntimeException("El DNI " + dniApoderado + " ya pertenece a otro apoderado.");
                        }
                        apoderadoExistente.setDni(dniApoderado);
                    }

                    // 3. Actualizamos los datos del formulario (ej. Celular o Email que cambiaron)
                    apoderadoExistente.setNombre(dtoAp.getNombre());
                    apoderadoExistente.setApellidoPaterno(dtoAp.getApellidoPaterno());
                    apoderadoExistente.setApellidoMaterno(dtoAp.getApellidoMaterno());
                    apoderadoExistente.setCelular(dtoAp.getCelular());
                    apoderadoExistente.setEmail(dtoAp.getEmail());

                    // 4. Guardamos los cambios
                    apoderadoRepository.save(apoderadoExistente);
                }
            }
        }

        // =========================================================================
        // CASO A: El frontend mandó una lista de IDs de apoderados que ya existen
        // =========================================================================
        if (dto.getIdsApoderados() != null && !dto.getIdsApoderados().isEmpty()) {
            for (Integer idApoderado : dto.getIdsApoderados()) {
                Apoderado apoderadoExistente = apoderadoRepository.findById(idApoderado)
                        .orElseThrow(() -> new RuntimeException("El ID de apoderado " + idApoderado + " no existe."));

                // Creamos el enlace en la intermedia para este apoderado
                vincularAlumnoConApoderado(alumno, apoderadoExistente);
            }
        }

        // =========================================================================
        // CASO B: El frontend mandó una lista de formularios de apoderados nuevos
        // =========================================================================
        if (dto.getApoderadosNuevos() != null && !dto.getApoderadosNuevos().isEmpty()) {
            for (dtoApoderado dtoAp : dto.getApoderadosNuevos()) {
                if (dtoAp.getDni() != null) {
                    String dniApoderado = dtoAp.getDni().trim();
                    Apoderado apoderadoFinal;

                    // Si el DNI ya existe, lo recuperamos Y ACTUALIZAMOS sus datos de contacto
                    if (apoderadoRepository.existsByDni(dniApoderado)) {
                        apoderadoFinal = apoderadoRepository.findByDni(dniApoderado).get();

                        // Actualizamos los datos por si cambiaron en el formulario (ej. Celular o Email)
                        apoderadoFinal.setNombre(dtoAp.getNombre());
                        apoderadoFinal.setApellidoPaterno(dtoAp.getApellidoPaterno());
                        apoderadoFinal.setApellidoMaterno(dtoAp.getApellidoMaterno());
                        apoderadoFinal.setCelular(dtoAp.getCelular());
                        apoderadoFinal.setEmail(dtoAp.getEmail());

                        apoderadoFinal = apoderadoRepository.save(apoderadoFinal); // Guarda los cambios del padre
                    } else {
                        // Si no existe, registramos al nuevo apoderado
                        Apoderado nuevoApoderado = new Apoderado();
                        nuevoApoderado.setDni(dniApoderado);
                        nuevoApoderado.setNombre(dtoAp.getNombre());
                        nuevoApoderado.setApellidoPaterno(dtoAp.getApellidoPaterno());
                        nuevoApoderado.setApellidoMaterno(dtoAp.getApellidoMaterno());
                        nuevoApoderado.setCelular(dtoAp.getCelular());
                        nuevoApoderado.setEmail(dtoAp.getEmail());

                        apoderadoFinal = apoderadoRepository.save(nuevoApoderado);
                    }

                    // Creamos el enlace en la intermedia para este apoderado (nuevo o recuperado)
                    vincularAlumnoConApoderado(alumno, apoderadoFinal);
                }
            }
        }
    }

    // Método auxiliar interno para no repetir la lógica de guardado en la tabla intermedia
    private void vincularAlumnoConApoderado(Alumno alumno, Apoderado apoderado) {
        // 1. Validamos si ya existe exactamente este alumno asociado a este apoderado
        boolean existeRelacion = alumnoApoderadoRepository.existsByAlumnoAndApoderado(alumno, apoderado);

        // 2. Solo si NO existe la relación en la base de datos, procedemos a guardarla
        if (!existeRelacion) {
            AlumnoApoderado relacion = new AlumnoApoderado();
            relacion.setAlumno(alumno);
            relacion.setApoderado(apoderado);
            alumnoApoderadoRepository.save(relacion);
        }
    }

    // =========================================================================
    // EL METODO QUE TE FALTABA: Convierte la entidad de la BD a tu dtoAlumno
    // =========================================================================
    private dtoAlumno convertirADto(Alumno alumno) {
        dtoAlumno dto = new dtoAlumno();
        dto.setIdAlumno(alumno.getIdAlumno());
        dto.setNombre(alumno.getNombre());
        dto.setApellidoPaterno(alumno.getApellidoPaterno());
        dto.setApellidoMaterno(alumno.getApellidoMaterno());
        dto.setCodigoUnico(alumno.getCodigoUnico());
        dto.setRutaFoto(alumno.getRutaFoto());
        dto.setEstado(alumno.getEstado());
        dto.setDni(alumno.getDni());

        if (alumno.getGrado() != null) dto.setIdGrado(alumno.getGrado().getIdGrado());
        if (alumno.getSeccion() != null) dto.setIdSeccion(alumno.getSeccion().getIdSeccion());

        // Mapeamos los apoderados asignados recorriendo la intermedia
        if (alumno.getAlumnoApoderados() != null) {
            List<dtoApoderado> listaApoderados = alumno.getAlumnoApoderados().stream()
                    .map(relacion -> {
                        Apoderado ap = relacion.getApoderado();
                        dtoApoderado dtoAp = new dtoApoderado();
                        dtoAp.setDni(ap.getDni());
                        dtoAp.setNombre(ap.getNombre());
                        dtoAp.setApellidoPaterno(ap.getApellidoPaterno());
                        dtoAp.setApellidoMaterno(ap.getApellidoMaterno());
                        dtoAp.setCelular(ap.getCelular());
                        dtoAp.setEmail(ap.getEmail());
                        return dtoAp;
                    }).toList();

            dto.setApoderadosAsignados(listaApoderados);
        }

        return dto;
    }
}
