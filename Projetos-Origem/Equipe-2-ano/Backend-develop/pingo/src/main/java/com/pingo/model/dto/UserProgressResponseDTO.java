package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class UserProgressResponseDTO {
    @NotBlank
    private String nickname;
    @NotBlank
    private int currentStage;

    public UserProgressResponseDTO(String nickname, int currentStage) {
        this.nickname = nickname;
        this.currentStage = currentStage;
    }

    public String getNickname() {
        return nickname;
    }

    public int getCurrentStage() {
        return currentStage;
    }
}
