package com.pingo.model.entity;

public enum DeviceType {
    INITIAL(0),
    CENTRAL(1),
    FINAL(2),
    COMMON(3);

    private final int value;
    
    DeviceType(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public static DeviceType fromDbValue(int value) {
        for (DeviceType type : DeviceType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Valor de tipo de dispositivo inválido: " + value);
    }
}
