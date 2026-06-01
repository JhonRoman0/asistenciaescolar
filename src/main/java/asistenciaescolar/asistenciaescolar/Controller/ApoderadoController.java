package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Dto.dtoApoderado;
import asistenciaescolar.asistenciaescolar.Service.ApoderadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apoderados")
public class ApoderadoController {

    @Autowired
    private ApoderadoService apoderadoService;


    @GetMapping("/dni/{dni}")
    public ResponseEntity<dtoApoderado> obtenerPorDni(@PathVariable String dni) {
        return ResponseEntity.ok(apoderadoService.buscarPorDni(dni));
    }

    @GetMapping
    public ResponseEntity<List<dtoApoderado>> listar() {
        return ResponseEntity.ok(apoderadoService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<dtoApoderado> registrar(@RequestBody dtoApoderado dto) {
        return ResponseEntity.ok(apoderadoService.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<dtoApoderado> actualizar(@PathVariable Integer id, @RequestBody dtoApoderado dto) {
        return ResponseEntity.ok(apoderadoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        apoderadoService.eliminarLogico(id);
        return ResponseEntity.ok("Apoderado dado de baja correctamente en el sistema.");
    }
}