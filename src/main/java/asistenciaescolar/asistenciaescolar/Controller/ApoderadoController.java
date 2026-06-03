package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Dto.dtoApoderado;
import asistenciaescolar.asistenciaescolar.Service.ApoderadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apoderados")

@Tag(name = "Apoderado", description = "Crud de Apoderado")
public class ApoderadoController {

    @Autowired
    private ApoderadoService apoderadoService;


    @GetMapping("/dni/{dni}")
    @Operation(summary = "Buscar Apoderados por DNI")
    public ResponseEntity<dtoApoderado> obtenerPorDni(@PathVariable String dni) {
        return ResponseEntity.ok(apoderadoService.buscarPorDni(dni));
    }

    @GetMapping
    @Operation(summary = "Listra Apoderados")
    public ResponseEntity<List<dtoApoderado>> listar() {
        return ResponseEntity.ok(apoderadoService.listarTodos());
    }

    @PostMapping
    @Operation(summary = "Reistrar Apoderado")
    public ResponseEntity<dtoApoderado> registrar(@RequestBody dtoApoderado dto) {
        return ResponseEntity.ok(apoderadoService.guardar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Apoderado")
    public ResponseEntity<dtoApoderado> actualizar(@PathVariable Integer id, @RequestBody dtoApoderado dto) {
        return ResponseEntity.ok(apoderadoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Apoderado Logica")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        apoderadoService.eliminarLogico(id);
        return ResponseEntity.ok("Apoderado dado de baja correctamente en el sistema.");
    }
}