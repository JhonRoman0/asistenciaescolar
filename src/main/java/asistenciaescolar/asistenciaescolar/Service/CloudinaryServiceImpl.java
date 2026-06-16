package asistenciaescolar.asistenciaescolar.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements StorageService {

    // Spring inyectará automáticamente el Bean que creamos en CloudinaryConfig gracias a RequiredArgsConstructor
    private final Cloudinary cloudinary;

    @Override
    public Map<String, Object> uploadFile(MultipartFile file, String folder) {
        // Validamos que el archivo no esté vacío antes de enviarlo
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo enviado no puede estar vacío.");
        }

        // Configuramos las opciones de subida (aquí manejamos la estructura por archivos/carpetas)
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder,
                "overwrite", true,
                "resource_type", "image"
        );

        try {
            // Subimos los bytes directamente en memoria sin crear archivos locales temporales
            return cloudinary.uploader().upload(file.getBytes(), options);
        } catch (IOException e) {
            // Transformamos el error de infraestructura/red en un 500 controlado
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error de conexión con Cloudinary al subir la imagen: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> deleteFile(String publicId)  {
        if (publicId == null || publicId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El identificador de la foto (publicId) no puede estar vacío.");
        }

        try {
            // Enviamos la orden de eliminación usando el ID único de la foto
            return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            // Transformamos el error de infraestructura/red en un 500 controlado
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error de conexión con Cloudinary al eliminar la imagen: " + e.getMessage());
        }
    }
}
