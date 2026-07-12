package asistenciaescolar.asistenciaescolar.Controller;
import asistenciaescolar.asistenciaescolar.Dto.dtoColegio;
import asistenciaescolar.asistenciaescolar.Model.Colegio;
import asistenciaescolar.asistenciaescolar.Service.ColegioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/colegios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ColegioController {

    private final ColegioService colegioService;

    @PostMapping
    public ResponseEntity<Colegio> crear(@RequestBody dtoColegio dto) {
            Colegio nuevo = colegioService.registrarColegioYTurnos(dto);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    // RETORNA LA LISTA CON DETALLES (Shape completo)
    @GetMapping
    public ResponseEntity<List<dtoColegio>> listar() {
        return new ResponseEntity<>(colegioService.obtenerTodosConDetalle(), HttpStatus.OK);
    }

    // RETORNA EL COLEGIO POR ID CON DETALLES (Shape completo)
    @GetMapping("/{id}")
    public ResponseEntity<dtoColegio> obtenerPorId(@PathVariable Integer id) {
        dtoColegio dto = colegioService.obtenerPorIdConDetalle(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Colegio> actualizar(@PathVariable Integer id, @RequestBody dtoColegio dto) {
            Colegio actualizado = colegioService.actualizarColegioYTurnos(id, dto);
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
    }
}
