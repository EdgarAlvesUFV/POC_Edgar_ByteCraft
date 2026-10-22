package com.pingo.model.dto;

import com.pingo.model.entity.DifficultLevel;

public class DifficultRequestDTO {
    private int noiseAmount;
    private int lightAmount;
    private float speed;
    private DifficultLevel level;

    public DifficultRequestDTO(int noiseAmount, int lightAmount, float speed, DifficultLevel level) {
        this.noiseAmount = noiseAmount;
        this.lightAmount = lightAmount;
        this.speed = speed;
        this.level = level;
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
