package app.controller;

import app.model.dto.ProfessorRequestDTO;
import app.model.dto.ProfessorResponseDTO;
import app.service.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/professores")
@CrossOrigin(origins = "http://localhost:5173")
public class ProfessorController {

    private final ProfessorService service;

    public ProfessorController(ProfessorService service) {
        this.service = service;
    }

    @PostMapping("/cadastrar")
    public ProfessorResponseDTO cadastrarProfessor(@RequestBody @Valid ProfessorRequestDTO dto) {
        return service.cadastrar(dto);
    }
}
