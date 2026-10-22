package app.controller;

import app.model.dto.TentativaCreateDto;
import app.service.TentativaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tentativas")
@RequiredArgsConstructor
public class TentativaController {

    private final TentativaService tentativaService;

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody TentativaCreateDto dto) {
        Long id = tentativaService.registrarTentativa(dto);
        return ResponseEntity.ok().body(
                java.util.Map.of(
                        "status", "ok",
                        "id", id
                )
        );
    }
}
