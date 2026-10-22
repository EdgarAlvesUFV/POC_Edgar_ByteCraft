package com.pingo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.pingo.model.dto.ScoreCreateRequestDTO;
import com.pingo.model.dto.ScoreResponseDTO;
import com.pingo.model.dto.ScoreUpdateRequestDTO;
import com.pingo.model.dto.TotalScoreResponseDTO;
import com.pingo.service.interfaces.ScoreService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/score")
public class ScoreController {
    
    @Autowired
    private ScoreService scoreService;

    @PostMapping("/create")
    public ScoreResponseDTO postMethodName(@RequestBody ScoreCreateRequestDTO request) {
        return scoreService.createScore(request);
    }

    @PatchMapping("/update")
    public ScoreResponseDTO updateScore(@RequestBody ScoreUpdateRequestDTO request) {
        return scoreService.updateScore(request);
    }

    @GetMapping("/get/{userId}/{stageId}")
    public ScoreResponseDTO getScoreByUserAndStage(@PathVariable int userId, @PathVariable int stageId) {
        return scoreService.getScoreByUserAndStage(userId, stageId);
    }

    @GetMapping("/total/{userId}")
    public TotalScoreResponseDTO getTotalScoreByUserId(@PathVariable int userId) {
        return scoreService.getTotalScoreByUserId(userId);
    }
    
}
