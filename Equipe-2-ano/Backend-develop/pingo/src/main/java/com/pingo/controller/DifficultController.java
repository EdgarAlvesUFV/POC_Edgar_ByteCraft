package com.pingo.controller;

import com.pingo.model.dto.DifficultRequestDTO;
import com.pingo.model.dto.DifficultResponseDTO;
import com.pingo.service.interfaces.DifficultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/difficult")
public class DifficultController {

    @Autowired
    private DifficultService difficultService;

    @PostMapping("/create")
    public ResponseEntity<DifficultResponseDTO> createDifficult(@RequestBody DifficultRequestDTO difficultRequestDTO) {
        DifficultResponseDTO responseDTO = difficultService.createDifficult(difficultRequestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<List<DifficultResponseDTO>> listDifficulties() {
        List<DifficultResponseDTO> difficulties = difficultService.listDifficulties();
        return ResponseEntity.ok(difficulties);
    }

}