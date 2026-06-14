package asistenciaescolar.asistenciaescolar.Service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

public interface StorageService {

    /**
     * Sube un archivo en memoria a la nube y lo organiza en una carpeta específica.
     * @param file El archivo multipart recibido desde el controlador (Angular).
     * @param folder Name de la carpeta en Cloudinary (ej: "alumnos", "usuarios").
     * @return Un mapa con la metadata de la respuesta (contiene la URL y el public_id).
     */
    Map<String, Object> uploadFile(MultipartFile file, String folder) throws IOException;

    /**
     * Elimina un archivo de la nube usando su identificador único.
     * @param publicId El ID único del archivo en Cloudinary.
     * @return El resultado de la operación en un mapa.
     */
    Map<String, Object> deleteFile(String publicId) throws IOException;
}