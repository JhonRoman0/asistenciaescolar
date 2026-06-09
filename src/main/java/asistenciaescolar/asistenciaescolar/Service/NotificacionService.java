package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Model.Alumno;
import asistenciaescolar.asistenciaescolar.Model.Asistencias;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    @Async
    public void enviarAlertaPadre(Alumno alumno, Asistencias asistencia) {
        try {
            String nombreCompleto = alumno.getNombre() + " " + alumno.getApellidoPaterno();
            String estadoStr = asistencia.getEstado().getEstado(); // "Asistencia", "Tardanza" o "Asistencia Justificada"
            String hora = asistencia.getHoraEntrada().toString();

            String mensaje = String.format(
                    "Control Escolar: El estudiante %s registró su ingreso como [%s] a las %s.",
                    nombreCompleto, estadoStr, hora
            );

            System.out.println("====== [HILO SECUNDARIO] INICIANDO ENVÍO DE NOTIFICACIÓN ======");
            System.out.println("Enviando mensaje para: " + nombreCompleto + " | Contenido: " + mensaje);

            // =========================================================================
            // APARTADO PARA IMPLEMENTAR LA API DE LA EMPRESA EXTERNA
            // =========================================================================
            // Aquí pegarás el código HTTP/Client que te provea la empresa externa.
            // Ejemplo:
            // apiCliente.sendSMS(alumno.getTelefonoApoderado(), mensaje);
            // =========================================================================

            System.out.println("====== [HILO SECUNDARIO] NOTIFICACIÓN PROCESADA ======");

        } catch (Exception e) {
            // Capturamos cualquier error de la API externa para que no afecte al sistema principal
            System.err.println("Error al enviar la notificación externa: " + e.getMessage());
        }
    }
}