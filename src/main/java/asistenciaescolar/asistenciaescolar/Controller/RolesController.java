package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Model.Roles;
import asistenciaescolar.asistenciaescolar.Service.RolesService; // Asegúrate que el nombre coincida
import asistenciaescolar.asistenciaescolar.Dto.dtoRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")

@Tag(name = "Roles", description = "Crud de Roles")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @GetMapping
    @Operation(summary = "Listar Roles")
    public ResponseEntity<List<Roles>> listar() {
        return ResponseEntity.ok(rolesService.listarTodos());
    }

    @PostMapping
    @Operation(summary = "Reistrar Roles")
    public ResponseEntity<String> crear(@RequestBody dtoRoles rolDto) {
        rolesService.crearRol(rolDto);
        return ResponseEntity.ok("Rol guardado correctamente");
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Roles")
    public ResponseEntity<String> actualizar(@PathVariable Integer id, @RequestBody dtoRoles rolDto) {
        try {
            rolesService.actualizarRol(id, rolDto);
            return ResponseEntity.ok("Rol actualizado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Rol no encontrado");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Roles")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        try {
            rolesService.eliminarLogico(id);
            return ResponseEntity.ok("Rol desactivado con éxito");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("No se puede")) {
                return ResponseEntity.status(400).body(e.getMessage());
            }
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}