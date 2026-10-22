
package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class TotalScoreResponseDTO {
    @NotBlank
    private int userId;
    @NotBlank
    private int points;

    public TotalScoreResponseDTO(int userId, int points) {
        this.userId = userId;
        this.points = points;
    }

    public int getUserId() {
        return userId;
    }

    public int getPoints() {
        return points;
    }
}