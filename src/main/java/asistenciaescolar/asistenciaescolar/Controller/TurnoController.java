package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Dto.dtoTurno;
import asistenciaescolar.asistenciaescolar.Model.Turno;
import asistenciaescolar.asistenciaescolar.Service.TurnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turnos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;

    @PostMapping
    public ResponseEntity<Turno> crear(@RequestBody dtoTurno dto) {
        // Deja fluir las excepciones de validación o lógica del Service directamente al frontend
        Turno nuevoTurno = turnoService.crearTurno(dto);
        return new ResponseEntity<>(nuevoTurno, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Turno>> listarTodos() {
        return new ResponseEntity<>(turnoService.obtenerTodosLosTurnos(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Turno> obtenerPorId(@PathVariable Integer id) {
        // Quitando el comodín <?>. Si el id no existe, el Service lanzará el ResponseStatusException correspondiente.
        Turno turno = turnoService.obtenerTurnoPorId(id);
        return new ResponseEntity<>(turno, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Turno> actualizar(@PathVariable Integer id, @RequestBody dtoTurno dto) {
        // Eliminado el catch redundante que alteraba los estados HTTP reales
        Turno turnoActualizado = turnoService.actualizarTurno(id, dto);
        return new ResponseEntity<>(turnoActualizado, HttpStatus.OK);
    }
}