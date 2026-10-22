package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class ScoreResponseDTO {
    @NotBlank
    private int id;
    @NotBlank
    private int points;
    @NotBlank
    private int stageId;
    @NotBlank
    private int userId;

    public ScoreResponseDTO(int id, int points, int stageId, int userId) {
        this.id = id;
        this.points = points;
        this.stageId = stageId;
        this.userId = userId;
    }
    
    public int getId() {
        return id;
    }

    public int getPoints() {
        return points;
    }

    public int getStageId() {
        return stageId;
    }

    public int getUserId() {
        return userId;
    }
}
