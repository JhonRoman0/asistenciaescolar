package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Model.Roles;
import asistenciaescolar.asistenciaescolar.Service.RolesService; // Asegúrate que el nombre coincida
import asistenciaescolar.asistenciaescolar.Dto.dtoRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*") // Importante para conectar con tu HTML
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @GetMapping
    public ResponseEntity<List<Roles>> listar() {
        return ResponseEntity.ok(rolesService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody dtoRoles rolDto) {
        rolesService.crearRol(rolDto);
        return ResponseEntity.ok("Rol guardado correctamente");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Integer id, @RequestBody dtoRoles rolDto) {
        try {
            rolesService.actualizarRol(id, rolDto);
            return ResponseEntity.ok("Rol actualizado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Rol no encontrado");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        try {
            rolesService.eliminarLogico(id);
            return ResponseEntity.ok("Rol desactivado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Rol no encontrado");
        }
    }
}