package com.pingo.model.entity;

public class NetworkDevice {
    int id;
    String name;
    boolean hasDeffect;
    DeviceType type;
    String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getHasDeffect() {
        return hasDeffect;
    }

    public void setHasDeffect(boolean hasDeffect) {
        this.hasDeffect = hasDeffect;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
