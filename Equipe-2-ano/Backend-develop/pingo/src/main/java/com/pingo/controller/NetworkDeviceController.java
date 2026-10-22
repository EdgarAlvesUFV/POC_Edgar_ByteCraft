package com.pingo.controller;

import com.pingo.model.dto.PhaseSetupResponseDTO;
import com.pingo.service.interfaces.NetworkDeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/network-devices")
public class NetworkDeviceController {
    private final NetworkDeviceService networkDeviceService;

    public NetworkDeviceController(NetworkDeviceService networkDeviceService) {
        this.networkDeviceService = networkDeviceService;
    }

    @GetMapping("/phase-setup")
    public ResponseEntity<PhaseSetupResponseDTO> getPhaseSetup() {
        return ResponseEntity.ok(networkDeviceService.getPhaseSetup());
    }
}