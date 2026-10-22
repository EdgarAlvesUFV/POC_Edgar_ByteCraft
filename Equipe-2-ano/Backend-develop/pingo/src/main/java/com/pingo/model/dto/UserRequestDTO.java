package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class UserRequestDTO {
    @NotBlank()
    private String nickname;
    @NotBlank()
    private String password;

    public UserRequestDTO(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }
}
