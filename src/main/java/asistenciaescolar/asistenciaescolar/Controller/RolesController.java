package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Dto.dtoRolesResponse;
import asistenciaescolar.asistenciaescolar.Model.Roles;
import asistenciaescolar.asistenciaescolar.Service.RolesService; // Asegúrate que el nombre coincida
import asistenciaescolar.asistenciaescolar.Dto.dtoRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Crud de Roles")
public class RolesController {

    private final RolesService rolesService;

    @GetMapping
    @Operation(summary = "Listar Roles")
    public ResponseEntity<List<dtoRolesResponse>> listar() {
        return ResponseEntity.ok(rolesService.listarTodos());
    }

    @PostMapping
    @Operation(summary = "Reistrar Roles")
    public ResponseEntity<Roles> crear(@RequestBody dtoRoles rolDto) {
        Roles nuevoRol = rolesService.crearRol(rolDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRol);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Roles")
    public ResponseEntity<Roles> actualizar(@PathVariable Integer id, @RequestBody dtoRoles rolDto) {
        Roles rolActualizado = rolesService.actualizarRol(id, rolDto);
        return ResponseEntity.ok(rolActualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Roles")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        rolesService.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }
}