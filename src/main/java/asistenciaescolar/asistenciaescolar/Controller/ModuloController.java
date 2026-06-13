package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Dto.dtoModulo;
import asistenciaescolar.asistenciaescolar.Model.Modulo;
import asistenciaescolar.asistenciaescolar.Service.ModuloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modulos")
@CrossOrigin(origins = "*")

@Tag(name = "Modulo", description = "Crud de Modulo")
public class ModuloController {

    @Autowired
    private ModuloService moduloService;

    @GetMapping
    @Operation(summary = "Listar Modulo")
    public ResponseEntity<List<Modulo>> listar() {
        List<Modulo> lista = moduloService.listarTodos();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar Modulo")
    public ResponseEntity<Modulo> buscarPorId(@PathVariable Integer id) {
        return moduloService.buscarPorId(id)
                .map(modulo -> new ResponseEntity<>(modulo, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
        try {
            Modulo moduloActualizado = moduloService.actualizar(id, dto);
            return new ResponseEntity<>(moduloActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar modulo de manera logica")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            moduloService.eliminarLogico(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
