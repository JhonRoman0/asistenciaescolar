package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Dto.dtoModulo;
import asistenciaescolar.asistenciaescolar.Model.Modulo;
import asistenciaescolar.asistenciaescolar.Service.ModuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modulos")
@CrossOrigin(origins = "*") // Permite peticiones desde el frontend (Angular, React, etc.)
public class ModuloController {

    @Autowired
    private ModuloService moduloService; // Inyección directa de tu clase de servicio

    // 1. GET: Listar todos los módulos
    @GetMapping
    public ResponseEntity<List<Modulo>> listar() {
        List<Modulo> lista = moduloService.listarTodos();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    // 2. GET: Buscar un módulo por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Modulo> buscarPorId(@PathVariable Integer id) {
        return moduloService.buscarPorId(id)
                .map(modulo -> new ResponseEntity<>(modulo, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 3. POST: Crear un nuevo módulo (Usa tu dtoModulo)
    @PostMapping
    public ResponseEntity<Modulo> crear(@RequestBody dtoModulo dto) {
        Modulo nuevoModulo = moduloService.guardar(dto);
        return new ResponseEntity<>(nuevoModulo, HttpStatus.CREATED);
    }

    // 4. PUT: Actualizar un módulo existente (Usa tu dtoModulo)
    @PutMapping("/{id}")
    public ResponseEntity<Modulo> actualizar(@PathVariable Integer id, @RequestBody dtoModulo dto) {
        try {
            Modulo moduloActualizado = moduloService.actualizar(id, dto);
            return new ResponseEntity<>(moduloActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 5. DELETE: Cambio de estado a 2 (Eliminación lógica)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            moduloService.eliminarLogico(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Código 204: Todo salió bien, sin contenido que devolver
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
