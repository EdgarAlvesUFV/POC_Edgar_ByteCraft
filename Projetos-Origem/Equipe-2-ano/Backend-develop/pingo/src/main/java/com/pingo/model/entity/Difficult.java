package com.pingo.model.entity;

public class Difficult {
    private int id;
    private int noiseAmount;
    private int lightAmount;
    private float speed;
    private DifficultLevel level;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getNoiseAmount() {
        return noiseAmount;
    }
    public void setNoiseAmount(int noiseAmount) {
        this.noiseAmount = noiseAmount;
    }
    public int getLightAmount() {
        return lightAmount;
    }
    public void setLightAmount(int lightAmount) {
        this.lightAmount = lightAmount;
    }
    public float getSpeed() {
        return speed;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public DifficultLevel getLevel() {
        return level;
    }
    public void setLevel(DifficultLevel level) {
        this.level = level;
    }
}
