package com.pingo.model.dto;

import java.util.List;

public class PhaseSetupResponseDTO {
    private List<NetworkDeviceResponseDTO> devices;
    private List<List<int[]>> correctPaths;

    public PhaseSetupResponseDTO(List<NetworkDeviceResponseDTO> devices, List<List<int[]>> correctPaths) {
        this.devices = devices;
        this.correctPaths = correctPaths;
    }

    public List<NetworkDeviceResponseDTO> getDevices() {
        return devices;
    }

    public List<List<int[]>> getCorrectPaths() {
        return correctPaths;
    }
}
