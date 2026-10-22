package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class ScoreUpdateRequestDTO {
    @NotBlank
    private int points;
    @NotBlank
    private int stageId;
    @NotBlank
    private int userId;

    public ScoreUpdateRequestDTO(int points, int stageId, int userId) {
        this.points = points;
        this.stageId = stageId;
        this.userId = userId;
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
