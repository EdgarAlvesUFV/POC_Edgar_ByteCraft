package com.pingo.service;

import com.pingo.dao.interfaces.DifficultDAO;
import com.pingo.model.dto.DifficultRequestDTO;
import com.pingo.model.dto.DifficultResponseDTO;
import com.pingo.model.entity.Difficult;
import com.pingo.service.interfaces.DifficultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DifficultServiceImpl implements DifficultService {

    @Autowired
    private DifficultDAO difficultDAO;

    @Override
    public DifficultResponseDTO createDifficult(DifficultRequestDTO difficultRequestDTO) {
        Difficult difficult = new Difficult();
        difficult.setNoiseAmount(difficultRequestDTO.getNoiseAmount());
        difficult.setLightAmount(difficultRequestDTO.getLightAmount());
        difficult.setSpeed(difficultRequestDTO.getSpeed());
        difficult.setLevel(difficultRequestDTO.getLevel());
        
        difficultDAO.save(difficult);
        return convertToResponseDTO(difficult);
    }

    @Override
    public List<DifficultResponseDTO> listDifficulties() {
        return difficultDAO.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private DifficultResponseDTO convertToResponseDTO(Difficult difficult) {
        DifficultResponseDTO responseDTO = new DifficultResponseDTO(difficult.getId(), difficult.getNoiseAmount(), difficult.getLightAmount(), difficult.getSpeed(), difficult.getLevel());
        return responseDTO;
    }
}