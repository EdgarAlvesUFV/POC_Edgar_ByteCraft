package com.pingo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.pingo.dao.StageDAOImpl;
import com.pingo.model.dto.StageRequestDTO;
import com.pingo.model.dto.StageResponseDTO;
import com.pingo.model.entity.Stage;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {
    @Mock
    private StageDAOImpl stageDAO;

    @InjectMocks
    private StageServiceImpl stageService;

    @Test
    void createStage_WhenStageNotExist_ShouldCreateStage() {
        // given
        StageRequestDTO request = new StageRequestDTO("Fase 1", 1);

        when(stageDAO.findByName("Fase 1")).thenReturn(null);
        when(stageDAO.findByNumber(1)).thenReturn(null);
        // when
        String result = stageService.createStage(request);

        // then
        verify(stageDAO, times(1)).findByName("Fase 1");
        verify(stageDAO, times(1)).findByNumber(1);
        assertEquals("Fase criada com sucesso", result);
    }

    @Test
    void createStage_WhenNameAlreadyExist_ShouldForbbiden() {
        Stage stage = new Stage();
        StageRequestDTO request = new StageRequestDTO("Fase 1", 1);
        stage.setName(request.getName());
        stage.setNumber(request.getNumber());

        when(stageDAO.findByName("Fase 1")).thenReturn(stage);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> stageService.createStage(request)
        );

        assertEquals("403 FORBIDDEN \"Fase já existente\"", exception.getMessage());
        verify(stageDAO, times(1)).findByName("Fase 1");
        verify(stageDAO, never()).save(any());
    }

    @Test
    void createStage_WhenNumberAlreadyExist_ShouldForbbiden() {
        Stage stage = new Stage();
        StageRequestDTO request = new StageRequestDTO("Fase 1", 1);
        stage.setName(request.getName());
        stage.setNumber(request.getNumber());

        when(stageDAO.findByName("Fase 1")).thenReturn(null);
        when(stageDAO.findByNumber(1)).thenReturn(stage);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> stageService.createStage(request)
        );

        assertEquals("403 FORBIDDEN \"Fase já existente\"", exception.getMessage());
        verify(stageDAO, times(1)).findByName("Fase 1");
        verify(stageDAO, never()).save(any());
    }

    @Test
    void listStages_WhenExistStages_ShouldReturnStages() {
        // given
        Stage stage1 = new Stage();
        stage1.setName("Fase 1");
        stage1.setNumber(1);

        Stage stage2 = new Stage();
        stage2.setName("Fase 2");
        stage2.setNumber(2);

        when(stageDAO.findAll()).thenReturn(List.of(stage1, stage2));

        List<StageResponseDTO> result = stageService.listStages();

        verify(stageDAO, times(1)).findAll();
        assertEquals(2, result.size());
    }

    @Test
    void listStages_WhenNotExistStages_ShouldReturnEmptyList() {
        when(stageDAO.findAll()).thenReturn(null);

        List<StageResponseDTO> result = stageService.listStages();

        assertEquals(0, result.size());
        verify(stageDAO, times(1)).findAll();
    }

}
