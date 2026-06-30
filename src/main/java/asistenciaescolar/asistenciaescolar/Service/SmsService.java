package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Config.TwilioConfig;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final TwilioConfig twilioConfig;


    public void enviarSmsAsistencia(String celularApoderado, String nombreCompletoAlumno, LocalDateTime fechaHoraMarcado, String estadoAsistencia) {

        try {
            String numeroDestino = celularApoderado.startsWith("+51") ? celularApoderado : "+51" + celularApoderado.trim();

            // Formateadores dentro del servicio para limpiar la cadena del SMS
            DateTimeFormatter formateadorHora = DateTimeFormatter.ofPattern("HH:mm:ss");
            DateTimeFormatter formateadorFecha = DateTimeFormatter.ofPattern("dd/MM");

            String hora = fechaHoraMarcado.format(formateadorHora);
            String fecha = fechaHoraMarcado.format(formateadorFecha);

            String textoMensaje = String.format(
                    "Control Escolar: El alumno %s ha ingresado a las %s la fecha %s y su estado es %s.",
                    nombreCompletoAlumno,
                    hora,
                    fecha,
                    estadoAsistencia.toUpperCase()
            );

            Message.creator(
                    new PhoneNumber(numeroDestino),
                    new PhoneNumber(twilioConfig.getTwilioPhoneNumber()),
                    textoMensaje
            ).create();

            System.out.println("SMS enviado correctamente a: " + numeroDestino);

        } catch (Exception e) {
            System.err.println("Error al enviar el SMS a través de Twilio: " + e.getMessage());
        }
    }
}