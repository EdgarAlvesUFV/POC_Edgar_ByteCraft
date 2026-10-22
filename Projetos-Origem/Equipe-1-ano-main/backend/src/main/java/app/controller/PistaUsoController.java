package app.controller;

import app.model.dto.PistaUsoCreateDto;
import app.service.PistaUsoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pistauso")
@RequiredArgsConstructor
public class PistaUsoController {

    private final PistaUsoService pistaUsoService;

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody PistaUsoCreateDto dto) {
        Long id = pistaUsoService.registrarPistaUso(dto);
        return ResponseEntity.ok(
                java.util.Map.of(
                        "status", "ok",
                        "id", id
                )
        );
    }
}
