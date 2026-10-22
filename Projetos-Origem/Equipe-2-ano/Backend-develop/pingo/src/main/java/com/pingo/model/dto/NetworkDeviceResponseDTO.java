package com.pingo.model.dto;

import com.pingo.model.entity.DeviceType;

public class NetworkDeviceResponseDTO {
    private int id;
    private String name;
    private DeviceType type;
    private boolean hasDeffect;
    private int positionY;
    private int positionX;

    public NetworkDeviceResponseDTO(int id, String name, DeviceType type, boolean hasDeffect, int positionY, int positionX) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.hasDeffect = hasDeffect;
        this.positionY = positionY;
        this.positionX = positionX;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public DeviceType getType() {
        return type;
    }

    public boolean isHasDeffect() {
        return hasDeffect;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }
}
