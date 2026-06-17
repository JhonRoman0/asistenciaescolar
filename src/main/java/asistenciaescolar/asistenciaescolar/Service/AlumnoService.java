package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Config.DniApiException;
import asistenciaescolar.asistenciaescolar.Dto.DniData;
import asistenciaescolar.asistenciaescolar.Dto.DniResponse;
import asistenciaescolar.asistenciaescolar.Dto.dtoAlumno;
import asistenciaescolar.asistenciaescolar.Dto.dtoApoderado;
import asistenciaescolar.asistenciaescolar.Model.*;
import asistenciaescolar.asistenciaescolar.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

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

    @Autowired
    private RepositoryTurno turnoRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.dni.token}")
    private String apiToken;

    // 1. Método para REGISTRAR
    @Transactional
    public Alumno guardarAlumno(dtoAlumno dto, MultipartFile foto) {
        // 1. Validamos que el DNI único no exista para evitar errores de duplicidad
        if (alumnoRepository.existsByDni(dto.getDni())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El DNI " + dto.getDni() + " ya está registrado en el sistema.");
        }
        Alumno alumno = new Alumno();
        // 2. Si el frontend envía una foto, la subimos a su carpeta estructurada antes de guardar en BD
        if (foto != null && !foto.isEmpty()) {
            try {
                // Buscamos los nombres de grado y sección para la subcarpeta automática
                Grado grado = gradoRepository.findById(dto.getIdGrado())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grado no encontrado"));
                Seccion seccion = seccionRepository.findById(dto.getIdSeccion())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sección no encontrada"));

                // Limpiamos el texto (minúsculas, sin espacios) siguiendo buenas prácticas
                String folderEstructurado = "alumnos/" +
                        grado.getGrado().toLowerCase().trim().replace(" ", "-") + "/" +
                        seccion.getSeccion().toLowerCase().trim().replace(" ", "-");

                // Subimos a Cloudinary usando el servicio estructurado
                Map<String, Object> result = storageService.uploadFile(foto, folderEstructurado);

                // Extraemos la metadata que nos devuelve Cloudinary
                dto.setRutaFoto((String) result.get("secure_url"));
                alumno.setFotoPublicId((String) result.get("public_id"));

            } catch (ResponseStatusException e) {
                // Si el error fue un 404 de Grado/Sección, lo dejamos pasar directamente
                throw e;
            } catch (Exception e) {
                // Si falla la conexión con Cloudinary, devolvemos un INTERNAL_SERVER_ERROR controlado
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al subir la foto de perfil: " + e.getMessage());
            }
        } else {
        }
        Alumno alumnoGuardado = mapearYGuardar(alumno, dto);

        procesarYAsociarApoderado(alumnoGuardado, dto);

        return alumnoGuardado;
    }

    public dtoAlumno obtenerPorId(Integer id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado con ID: " + id));
        return convertirADto(alumno);
    }

    // 2. Método para ACTUALIZAR
    @Transactional
    public Alumno actualizarAlumno(Integer id, dtoAlumno dto, MultipartFile nuevaFoto) {
        Alumno alumnoExistente = alumnoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado con ID: " + id));
        if (!alumnoExistente.getDni().equals(dto.getDni()) && alumnoRepository.existsByDni(dto.getDni())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El DNI " + dto.getDni() + " ya pertenece a otro alumno.");
        }
        if (nuevaFoto != null && !nuevaFoto.isEmpty()) {
            try {
                if (alumnoExistente.getFotoPublicId() != null && !alumnoExistente.getFotoPublicId().isEmpty()) {
                    storageService.deleteFile(alumnoExistente.getFotoPublicId());
                }

                Grado grado = gradoRepository.findById(dto.getIdGrado())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grado no encontrado"));
                Seccion seccion = seccionRepository.findById(dto.getIdSeccion())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seccion no encontrado"));

                String folderEstructurado = "alumnos/" +
                        grado.getGrado().toLowerCase().trim().replace(" ", "-") + "/" +
                        seccion.getSeccion().toLowerCase().trim().replace(" ", "-");

                // 3. Subimos el nuevo archivo
                Map<String, Object> result = storageService.uploadFile(nuevaFoto, folderEstructurado);

                // 4. Actualizamos el DTO y la entidad con los nuevos valores
                dto.setRutaFoto((String) result.get("secure_url"));
                alumnoExistente.setFotoPublicId((String) result.get("public_id"));

            } catch (ResponseStatusException e) {
                throw e;
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar la foto: " + e.getMessage());
            }
        } else {
            dto.setRutaFoto(alumnoExistente.getRutaFoto());
        }
        Alumno alumnoActualizado = mapearYGuardar(alumnoExistente, dto);

        procesarYAsociarApoderado(alumnoActualizado, dto);

        return alumnoActualizado;
    }

    // Este es el que ahora llama tu controlador en la línea 25
    public List<dtoAlumno> listarAlumnosConApoderados(String nombre, Integer idGrado, Integer idSeccion, Integer estado) {
        String nombreBusqueda = (nombre != null && !nombre.trim().isEmpty()) ? nombre : null;
        List<Alumno> alumnos = alumnoRepository.buscarConFiltros(nombreBusqueda, idGrado, idSeccion, estado);
        return alumnos.stream().map(this::convertirADto).toList();
    }

    private String generarCodigoUnico() {
        String nuevoCodigo;
        boolean existe;
        do {
            int numero = (int)(Math.random() * 900000) + 100000;
            nuevoCodigo = "ALU" + numero;

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
        alumno.setFechaNaci(dto.getFechaNaci());


        if (dto.getRutaFoto() == null || dto.getRutaFoto().isEmpty()) {
            alumno.setRutaFoto("default-profile.png");
        } else {
            alumno.setRutaFoto(dto.getRutaFoto());
        }

        if (alumno.getIdAlumno() == null) {
            String codigoPlano = generarCodigoUnico();
            alumno.setCodigoUnico(codigoPlano);
            alumno.setCodigoHash(hashearTexto(codigoPlano));
        }

        alumno.setEstado(dto.getEstado() != null ? dto.getEstado() : 1);

        // Buscamos las entidades relacionadas
        Turno turno = turnoRepository.findById(dto.getIdTurno())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turno no encontrado"));
        Grado grado = gradoRepository.findById(dto.getIdGrado())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grado no encontrado"));
        Seccion seccion = seccionRepository.findById(dto.getIdSeccion())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seccion no encontrado"));

        alumno.setTurno(turno);
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el alumno con ID: " + id));

        // 2. Cambiamos solo el estado a 2
        alumno.setEstado(2);

        // 3. Guardamos los cambios
        alumnoRepository.save(alumno);
    }

    // Método privado auxiliar para reutilizar la lógica del apoderado tanto en crear como en editar
    private void procesarYAsociarApoderado(Alumno alumno, dtoAlumno dto) {

        // CASO C: (NUEVO) El frontend mandó los apoderados ya vinculados en la lista de 'apoderadosAsignados'
        if (dto.getApoderadosAsignados() != null && !dto.getApoderadosAsignados().isEmpty()) {
            for (dtoApoderado dtoAp : dto.getApoderadosAsignados()) {
                if (dtoAp.getIdApoderado() != null) {
                    // 1. Buscamos el apoderado existente por su ID
                    Apoderado apoderadoExistente = apoderadoRepository.findById(dtoAp.getIdApoderado())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El apoderado con ID " + dtoAp.getIdApoderado() + " no existe."));

                    // 2. Validamos que si se cambió el DNI en el input, no choque con otra persona en la BD
                    if (dtoAp.getDni() != null) {
                        String dniApoderado = dtoAp.getDni().trim();
                        String dniEnBD = apoderadoExistente.getDni().trim(); // <-- Limpiamos espacios de la BD

                        if (!dniEnBD.equals(dniApoderado) && apoderadoRepository.existsByDni(dniApoderado)) {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El DNI " + dniApoderado + " ya pertenece a otro apoderado.");
                        }
                        apoderadoExistente.setDni(dniApoderado);
                    }

                    // 3. Actualizamos los datos del formulario (ej. Celular o Email que cambiaron)
                    apoderadoExistente.setNombre(dtoAp.getNombre());
                    apoderadoExistente.setApellidoPaterno(dtoAp.getApellidoPaterno());
                    apoderadoExistente.setApellidoMaterno(dtoAp.getApellidoMaterno());
                    apoderadoExistente.setCelular(dtoAp.getCelular());
                    apoderadoExistente.setEmail(dtoAp.getEmail());
                    apoderadoRepository.save(apoderadoExistente);

                    // Buscamos la relación intermedia existente entre este alumno y este apoderado
                    AlumnoApoderado relacionIntermedia = alumnoApoderadoRepository.findByAlumnoAndApoderado(alumno, apoderadoExistente)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Relación alumno-apoderado no encontrada."));

                    // Si el frontend mandó el valor, lo actualizamos (si viene null, por defecto false)
                    relacionIntermedia.setEsPrincipal(dtoAp.getEsPrincipal() != null ? dtoAp.getEsPrincipal() : false);
                    alumnoApoderadoRepository.save(relacionIntermedia);
                }
            }
        }

        // CASO A: El frontend mandó una lista de IDs de apoderados que ya existen
        if (dto.getIdsApoderados() != null && !dto.getIdsApoderados().isEmpty()) {
            for (dtoApoderado dtoAp : dto.getIdsApoderados()) {
                Apoderado apoderadoExistente = apoderadoRepository.findById(dtoAp.getIdApoderado())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El ID de apoderado " + dtoAp.getIdApoderado() + " no existe."));

                // Si ya existe la relación, la recuperamos; si no, la creamos
                AlumnoApoderado relacion = alumnoApoderadoRepository.findByAlumnoAndApoderado(alumno, apoderadoExistente)
                        .orElse(new AlumnoApoderado(alumno, apoderadoExistente));

                // Seteamos si es principal o secundario
                relacion.setEsPrincipal(dtoAp.getEsPrincipal() != null ? dtoAp.getEsPrincipal() : false);
                alumnoApoderadoRepository.save(relacion);
            }
        }

        // CASO B: El frontend mandó una lista de formularios de apoderados nuevos
        if (dto.getApoderadosNuevos() != null && !dto.getApoderadosNuevos().isEmpty()) {
            for (dtoApoderado dtoAp : dto.getApoderadosNuevos()) {
                if (dtoAp.getDni() != null) {
                    String dniApoderado = dtoAp.getDni().trim();
                    Apoderado apoderadoFinal;

                    if (apoderadoRepository.existsByDni(dniApoderado)) {
                        apoderadoFinal = apoderadoRepository.findByDni(dniApoderado).get();

                        // Actualizamos los datos por si cambiaron en el formulario (ej. Celular o Email)
                        apoderadoFinal.setNombre(dtoAp.getNombre());
                        apoderadoFinal.setApellidoPaterno(dtoAp.getApellidoPaterno());
                        apoderadoFinal.setApellidoMaterno(dtoAp.getApellidoMaterno());
                        apoderadoFinal.setCelular(dtoAp.getCelular());
                        apoderadoFinal.setEmail(dtoAp.getEmail());

                        apoderadoFinal = apoderadoRepository.save(apoderadoFinal);
                    } else {
                        Apoderado nuevoApoderado = new Apoderado();
                        nuevoApoderado.setDni(dniApoderado);
                        nuevoApoderado.setNombre(dtoAp.getNombre());
                        nuevoApoderado.setApellidoPaterno(dtoAp.getApellidoPaterno());
                        nuevoApoderado.setApellidoMaterno(dtoAp.getApellidoMaterno());
                        nuevoApoderado.setCelular(dtoAp.getCelular());
                        nuevoApoderado.setEmail(dtoAp.getEmail());

                        apoderadoFinal = apoderadoRepository.save(nuevoApoderado);
                    }

                    AlumnoApoderado relacion = alumnoApoderadoRepository.findByAlumnoAndApoderado(alumno, apoderadoFinal)
                            .orElse(new AlumnoApoderado(alumno, apoderadoFinal));

                    relacion.setEsPrincipal(dtoAp.getEsPrincipal() != null ? dtoAp.getEsPrincipal() : false);
                    alumnoApoderadoRepository.save(relacion);
                }
            }
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
        dto.setCodigoHash(alumno.getCodigoHash());
        dto.setRutaFoto(alumno.getRutaFoto());
        dto.setEstado(alumno.getEstado());
        dto.setDni(alumno.getDni());
        dto.setFechaNaci(alumno.getFechaNaci());


        if (alumno.getTurno() != null) {
            dto.setTurno(alumno.getTurno());
            dto.setIdTurno(alumno.getTurno().getIdTurno());
        }

        if (alumno.getGrado() != null) {
            dto.setGrado(alumno.getGrado());
            dto.setIdGrado(alumno.getGrado().getIdGrado());
        }
        if (alumno.getSeccion() != null) {
            dto.setSeccion(alumno.getSeccion());
            dto.setIdSeccion(alumno.getSeccion().getIdSeccion());
        }

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
                        dtoAp.setEsPrincipal(relacion.getEsPrincipal());
                        return dtoAp;
                    }).toList();

            dto.setApoderadosAsignados(listaApoderados);
        }

        return dto;
    }

    // =========================================================================
// NUEVO MÉTODO AUXILIAR: Añade esto al final de tu clase AlumnoService
// =========================================================================
    private String hashearTexto(String texto) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(texto.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString(); // Retorna la cadena de 64 caracteres
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error crítico al generar el hash de seguridad.", ex);
        }
    }

    // Cambia DniResponse por DniData en la firma del método
    public DniData obtenerDatosPorDni(String dni) {
        String url = "https://apiperu.dev/api/dni/" + dni + "?api_token=" + apiToken;

        // Aquí recibes el contenedor completo (la caja)
        DniResponse respuesta = restTemplate.getForObject(url, DniResponse.class);

        if (respuesta != null && respuesta.isSuccess()) {
            return respuesta.getData(); // Esto ahora sí coincide con DniData
        } else {
            throw new DniApiException("No se encontró el DNI o la API falló.");
        }
    }
}
