package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Dto.dtoModulo;
import asistenciaescolar.asistenciaescolar.Model.Modulo;
import asistenciaescolar.asistenciaescolar.Service.ModuloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modulos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Modulo", description = "Crud de Modulo")
public class ModuloController {

    private final ModuloService moduloService;

    @GetMapping
    @Operation(summary = "Listar Modulo")
    public ResponseEntity<List<Modulo>> listar() {
        List<Modulo> lista = moduloService.listarTodos();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar Modulo")
    public ResponseEntity<Modulo> buscarPorId(@PathVariable Integer id) {
        Modulo modulo = moduloService.buscarPorId(id);
        return new ResponseEntity<>(modulo, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Crear modulo")
    public ResponseEntity<Modulo> crear(@RequestBody dtoModulo dto) {
        Modulo nuevoModulo = moduloService.guardar(dto);
        return new ResponseEntity<>(nuevoModulo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar modulo")
    public ResponseEntity<Modulo> actualizar(@PathVariable Integer id, @RequestBody dtoModulo dto) {

            Modulo moduloActualizado = moduloService.actualizar(id, dto);
            return new ResponseEntity<>(moduloActualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar modulo de manera logica")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        moduloService.eliminarLogico(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
