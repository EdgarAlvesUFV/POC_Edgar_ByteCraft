package com.pingo.service.interfaces;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pingo.model.dto.StageRequestDTO;
import com.pingo.model.dto.StageResponseDTO;

@Service
public interface StageService {
    
    public String createStage(StageRequestDTO request);

    public List<StageResponseDTO> listStages();
}