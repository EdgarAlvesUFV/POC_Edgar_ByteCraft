package com.pingo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pingo.dao.interfaces.StageDAO;
import com.pingo.model.dto.StageRequestDTO;
import com.pingo.model.dto.StageResponseDTO;
import com.pingo.model.entity.Stage;
import com.pingo.service.interfaces.StageService;

@Service
public class StageServiceImpl implements StageService {
    private final StageDAO stageDAO;

    public StageServiceImpl(StageDAO stageDAO) {
        this.stageDAO = stageDAO;
    }

    @Override
    public String createStage(StageRequestDTO request) {
        Stage stage = stageDAO.findByName(request.getName());
        if(stage != null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Fase já existente");
        }
        stage = stageDAO.findByNumber(request.getNumber());
        if(stage != null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Fase já existente");
        }
        stage = new Stage();
        stage.setName(request.getName());
        stage.setNumber(request.getNumber());
        stageDAO.save(stage);
        return "Fase criada com sucesso";
    }

    @Override
    public List<StageResponseDTO> listStages() {
        List<Stage> stages = stageDAO.findAll();
        if(stages == null) {
            return List.of();
        }
        return stages.stream()
            .map(stage -> new StageResponseDTO(stage.getName(), stage.getNumber()))
            .collect(Collectors.toList());
    }
}
