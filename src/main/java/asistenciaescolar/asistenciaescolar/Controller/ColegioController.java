package asistenciaescolar.asistenciaescolar.Controller;
import asistenciaescolar.asistenciaescolar.Dto.dtoColegio;
import asistenciaescolar.asistenciaescolar.Model.Colegio;
import asistenciaescolar.asistenciaescolar.Service.ColegioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/colegios")
@CrossOrigin(origins = "*")
public class ColegioController {

    @Autowired
    private ColegioService colegioService;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody dtoColegio dto) {
        try {
            Colegio nuevo = colegioService.registrarColegioYTurnos(dto);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Colegio>> listar() {
        return new ResponseEntity<>(colegioService.obtenerTodos(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Integer id) {
        try {
            Colegio col = colegioService.obtenerPorId(id);
            return new ResponseEntity<>(col, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody dtoColegio dto) {
        try {
            Colegio actualizado = colegioService.actualizarColegioYTurnos(id, dto);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
