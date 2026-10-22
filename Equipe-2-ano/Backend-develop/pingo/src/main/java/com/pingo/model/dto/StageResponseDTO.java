package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class StageResponseDTO {
    @NotBlank
    private String name;
    @NotBlank
    private int number;

    public StageResponseDTO(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }
}