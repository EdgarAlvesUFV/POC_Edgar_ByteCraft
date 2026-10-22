package com.pingo.service.interfaces;

import com.pingo.model.dto.DifficultRequestDTO;
import com.pingo.model.dto.DifficultResponseDTO;
import java.util.List;

public interface DifficultService {
    DifficultResponseDTO createDifficult(DifficultRequestDTO difficultRequestDTO);
    List<DifficultResponseDTO> listDifficulties();
}