package com.pingo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pingo.model.dto.StageRequestDTO;
import com.pingo.model.dto.StageResponseDTO;
import com.pingo.service.interfaces.StageService;

@RestController
@RequestMapping("/stage")
public class StageController {
    private StageService stageService;

    public StageController(StageService stageService) {
        this.stageService = stageService;
    }   

    @PostMapping("/create")
    public String createStage(@RequestBody StageRequestDTO request) {
        return stageService.createStage(request);
    }

    @GetMapping("/list")
    public List<StageResponseDTO> listStages() {
        return stageService.listStages();
    }
}