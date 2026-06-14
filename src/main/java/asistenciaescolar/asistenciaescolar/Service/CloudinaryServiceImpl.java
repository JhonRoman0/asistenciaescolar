package asistenciaescolar.asistenciaescolar.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements StorageService {

    // Spring inyectará automáticamente el Bean que creamos en CloudinaryConfig gracias a RequiredArgsConstructor
    private final Cloudinary cloudinary;

    @Override
    public Map<String, Object> uploadFile(MultipartFile file, String folder) throws IOException {
        // Validamos que el archivo no esté vacío antes de enviarlo
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        // Configuramos las opciones de subida (aquí manejamos la estructura por archivos/carpetas)
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder,
                "overwrite", true,
                "resource_type", "image"
        );

        // Subimos los bytes directamente en memoria (.getBytes()) sin crear archivos locales en Ubuntu
        return cloudinary.uploader().upload(file.getBytes(), options);
    }

    @Override
    public Map<String, Object> deleteFile(String publicId) throws IOException {
        if (publicId == null || publicId.isEmpty()) {
            throw new IllegalArgumentException("El publicId no puede estar vacío");
        }

        // Enviamos la orden de eliminación usando el ID único de la foto
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
