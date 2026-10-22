
package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class ScoreCreateRequestDTO {
    @NotBlank
    private int userId;
    @NotBlank
    private int stageId;

    public ScoreCreateRequestDTO(int userId, int stageId) {
        this.userId = userId;
        this.stageId = stageId;
    }

    public int getUserId() {
        return userId;
    }

    public int getStageId() {
        return stageId;
    }
}