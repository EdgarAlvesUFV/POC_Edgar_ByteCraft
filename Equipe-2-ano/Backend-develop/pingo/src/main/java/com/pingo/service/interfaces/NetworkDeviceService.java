package com.pingo.service.interfaces;

import com.pingo.model.dto.NetworkDeviceResponseDTO;
import com.pingo.model.dto.PhaseSetupResponseDTO;
import java.util.List;

public interface NetworkDeviceService {
    PhaseSetupResponseDTO getPhaseSetup();
    public List<List<int[]>> defineValidPath(List<NetworkDeviceResponseDTO> devices);
}