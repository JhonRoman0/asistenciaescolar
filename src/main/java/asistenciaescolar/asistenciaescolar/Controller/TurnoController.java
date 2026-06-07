package asistenciaescolar.asistenciaescolar.Controller;

import asistenciaescolar.asistenciaescolar.Dto.dtoTurno;
import asistenciaescolar.asistenciaescolar.Model.Turno;
import asistenciaescolar.asistenciaescolar.Service.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turnos")
@CrossOrigin(origins = "*")
public class TurnoController {

    @Autowired
    private TurnoService repositoryTurno;

    @Autowired
    private TurnoService turnoService;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody dtoTurno dto) {
        try {
            Turno nuevoTurno = turnoService.crearTurno(dto);
            return new ResponseEntity<>(nuevoTurno, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Turno>> listarTodos() {
        return new ResponseEntity<>(turnoService.obtenerTodosLosTurnos(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Integer id) {
        try {
            Turno turno = turnoService.obtenerTurnoPorId(id);
            return new ResponseEntity<>(turno, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody dtoTurno dto) {
        try {
            Turno turnoActualizado = turnoService.actualizarTurno(id, dto);
            return new ResponseEntity<>(turnoActualizado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}