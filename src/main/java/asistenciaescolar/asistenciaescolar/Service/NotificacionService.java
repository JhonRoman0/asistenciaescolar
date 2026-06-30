    package asistenciaescolar.asistenciaescolar.Service;

    import asistenciaescolar.asistenciaescolar.Model.Alumno;
    import asistenciaescolar.asistenciaescolar.Model.Asistencias;
    import asistenciaescolar.asistenciaescolar.Model.Apoderado;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;

    @Service
    @RequiredArgsConstructor
    public class NotificacionService {

        private final SmsService smsService;

        public void enviarAlertaPadre(Alumno alumno, Asistencias asistencia) {
            System.out.println("====== [DEBUG SMS] Iniciando flujo masivo para el alumno: " + alumno.getNombre() + " " + alumno.getApellidoPaterno() + " ======");

            if (alumno.getAlumnoApoderados() == null || alumno.getAlumnoApoderados().isEmpty()) {
                System.err.println("====== [DEBUG SMS] ERROR: La lista de apoderados vinculados al alumno está vacía o es nula ======");
                return;
            }

            // RECORREMOS TODOS LOS APODERADOS SIN FILTRAR POR EL PRINCIPAL
            alumno.getAlumnoApoderados().forEach(relacion -> {
                Apoderado apoderado = relacion.getApoderado();

                System.out.println("====== [DEBUG SMS] Procesando apoderado: " + apoderado.getNombre() + " (Principal: " + relacion.getEsPrincipal() + ") ======");

                // Verificamos que este apoderado en particular tenga un celular registrado
                if (apoderado.getCelular() != null && !apoderado.getCelular().trim().isEmpty()) {

                    // Construimos el nombre completo del alumno
                    String nombreCompleto = String.format("%s %s %s",
                            alumno.getNombre(),
                            alumno.getApellidoPaterno(),
                            alumno.getApellidoMaterno() != null ? alumno.getApellidoMaterno() : ""
                    ).trim();

                    // Combinamos la Fecha actual y la Hora de Entrada
                    LocalDateTime fechaHoraMarcado = asistencia.getFecha().atTime(asistencia.getHoraEntrada());

                    // Obtenemos el texto del estado
                    String estadoTexto = asistencia.getEstado().getEstado();

                    System.out.println("====== [DEBUG SMS] Enviando mensaje a: " + apoderado.getNombre() + " - Celular: " + apoderado.getCelular() + " ======");

                    // Enviamos el SMS individual utilizando tu servicio de Twilio
                    smsService.enviarSmsAsistencia(
                            apoderado.getCelular(),
                            nombreCompleto,
                            fechaHoraMarcado,
                            estadoTexto
                    );
                } else {
                    System.err.println("====== [DEBUG SMS] ADVERTENCIA: El apoderado " + apoderado.getNombre() + " no tiene celular en la BD. Saltando... ======");
                }
            });
        }
    }