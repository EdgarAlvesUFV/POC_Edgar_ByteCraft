package com.pingo.model.dto;

import jakarta.validation.constraints.NotBlank;

public class StageRequestDTO {
    @NotBlank
    private String name;
    @NotBlank
    private int number;

    public StageRequestDTO(String name, int number) {
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