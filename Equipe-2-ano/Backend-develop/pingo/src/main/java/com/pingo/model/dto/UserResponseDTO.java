package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class UserResponseDTO {
    @NotBlank()
    private String nickname;
    @NotBlank()
    private String accessToken;

    public UserResponseDTO(String nickname, String accessToken) {
        this.nickname = nickname;
        this.accessToken = accessToken;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
