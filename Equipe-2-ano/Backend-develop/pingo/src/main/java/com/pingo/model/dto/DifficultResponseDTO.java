package com.pingo.model.dto;

import com.pingo.model.entity.DifficultLevel;

public class DifficultResponseDTO {
    private Integer id;
    private int noiseAmount;
    private int lightAmount;
    private float speed;
    private DifficultLevel level;

    public DifficultResponseDTO(Integer id, int noiseAmount, int lightAmount, float speed, DifficultLevel level) {
        this.id = id;
        this.noiseAmount = noiseAmount;
        this.lightAmount = lightAmount;
        this.speed = speed;
        this.level = level;
    }

    public Integer getId() {
        return id;
    }

    public int getNoiseAmount() {
        return noiseAmount;
    }

    public int getLightAmount() {
        return lightAmount;
    }

    public float getSpeed() {
        return speed;
    }

    public DifficultLevel getLevel() {
        return level;
    }
}
