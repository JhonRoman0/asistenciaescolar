package asistenciaescolar.asistenciaescolar.Service;

import asistenciaescolar.asistenciaescolar.Dto.dtoApoderado;
import asistenciaescolar.asistenciaescolar.Model.Apoderado;
import asistenciaescolar.asistenciaescolar.Repository.RepositoryApoderado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApoderadoService {

    @Autowired
    private RepositoryApoderado apoderadoRepository;

    // 🔍 BUSCAR POR DNI (Solo activos)
    public dtoApoderado buscarPorDni(String dni) {
        Apoderado apoderado = apoderadoRepository.findByDniAndEstado(dni, 1)
                .orElseThrow(() -> new RuntimeException("Apoderado activo con DNI " + dni + " no encontrado."));
        return convertirADto(apoderado);
    }

    // 📋 LISTAR TODOS (Solo activos)
    public List<dtoApoderado> listarTodos() {
        return apoderadoRepository.findByEstado(1).stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    // ➕ REGISTRAR APODERADO INDEPENDIENTE
    public dtoApoderado guardar(dtoApoderado dto) {
        // Validamos si ya existe un apoderado ACTIVO con ese mismo DNI
        if (apoderadoRepository.existsByDniAndEstado(dto.getDni(), 1)) {
            throw new RuntimeException("El DNI " + dto.getDni() + " ya está registrado y activo.");
        }

        Apoderado apoderado = new Apoderado();
        apoderado.setDni(dto.getDni());
        apoderado.setNombre(dto.getNombre());
        apoderado.setApellidoPaterno(dto.getApellidoPaterno());
        apoderado.setApellidoMaterno(dto.getApellidoMaterno());
        apoderado.setCelular(dto.getCelular());
        apoderado.setEmail(dto.getEmail());
        apoderado.setEstado(1); // Aseguramos que inicie como Activo

        Apoderado guardado = apoderadoRepository.save(apoderado);
        return convertirADto(guardado);
    }

    // ✏️ ACTUALIZAR
    public dtoApoderado actualizar(Integer id, dtoApoderado dto) {
        Apoderado apoderadoExistente = apoderadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apoderado no encontrado con ID: " + id));

        // Si intenta cambiar el DNI, validamos que el nuevo no esté ocupado por otro activo
        if (!apoderadoExistente.getDni().equals(dto.getDni()) &&
                apoderadoRepository.existsByDniAndEstado(dto.getDni(), 1)) {
            throw new RuntimeException("El DNI " + dto.getDni() + " ya pertenece a otro apoderado activo.");
        }

        apoderadoExistente.setDni(dto.getDni());
        apoderadoExistente.setNombre(dto.getNombre());
        apoderadoExistente.setApellidoPaterno(dto.getApellidoPaterno());
        apoderadoExistente.setApellidoMaterno(dto.getApellidoMaterno());
        apoderadoExistente.setCelular(dto.getCelular());
        apoderadoExistente.setEmail(dto.getEmail());

        Apoderado actualizado = apoderadoRepository.save(apoderadoExistente);
        return convertirADto(actualizado);
    }

    // ❌ BORRADO LÓGICO
    public void eliminarLogico(Integer id) {
        Apoderado apoderado = apoderadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se pudo eliminar. El ID no existe."));

        // Cambiamos el estado a 0 para "ocultarlo" del sistema
        apoderado.setEstado(0);
        apoderadoRepository.save(apoderado);
    }

    // 🔄 MÉTODO AUXILIAR DE MAPEADO (Entidad -> DTO)
    private dtoApoderado convertirADto(Apoderado apoderado) {
        dtoApoderado dto = new dtoApoderado();
        dto.setIdApoderado(apoderado.getIdApoderado());
        dto.setDni(apoderado.getDni());
        dto.setNombre(apoderado.getNombre());
        dto.setApellidoPaterno(apoderado.getApellidoPaterno());
        dto.setApellidoMaterno(apoderado.getApellidoMaterno());
        dto.setCelular(apoderado.getCelular());
        dto.setEmail(apoderado.getEmail());
        dto.setEsPrincipal(false); // Por defecto en consultas independientes
        return dto;
    }
}