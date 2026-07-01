package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Config.TwilioConfig;
import com.twilio.Twilio;
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

    public void enviarSmsAsistencia(String celularApoderado, String nombreAlumno, LocalDateTime fechaHora, String estado) {
        // Aseguramos el formato con +51
        String numeroLimpio = celularApoderado.trim();
        String numeroDestino = numeroLimpio.startsWith("+51") ? numeroLimpio : "+51" + numeroLimpio;

        // Formateamos la fecha para que se vea más humana en el mensaje (Ej: 27/06/2026 19:30)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaFormateada = fechaHora.format(formatter);

        // Tu texto de asistencia estructurado
        String textoAsistencia = String.format(
                "Control Escolar: El alumno %s registró su asistencia como [%s] el %s.",
                nombreAlumno,
                estado,
                fechaFormateada
        );

        try {
            // Inicializamos Twilio explícitamente con tus credenciales configuradas
            Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());

            // Enviamos el WhatsApp usando el Sandbox universal
            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + numeroDestino),       // Destinatario real
                    new PhoneNumber("whatsapp:+14155238886"),          // Número del Sandbox de Twilio
                    textoAsistencia
            ).create();

            System.out.println("====== [TWILIO SUCCESS] WhatsApp enviado con SID: " + message.getSid() + " ======");
        } catch (Exception e) {
            System.err.println("Error al enviar el WhatsApp a través de Twilio: " + e.getMessage());
        }
    }
}