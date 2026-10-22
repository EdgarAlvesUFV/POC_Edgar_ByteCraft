package com.pingo.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.server.ResponseStatusException;

import com.pingo.dao.StageDAOImpl;
import com.pingo.dao.UserDAOImpl;
import com.pingo.dao.UserProgressDAOImpl;

import com.pingo.model.dto.UserProgressResponseDTO;
import com.pingo.model.dto.UserProgressRequestDTO;
import com.pingo.model.entity.Stage;
import com.pingo.model.entity.User;
import com.pingo.model.entity.UserProgress;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserProgressServiceTest {
    @Mock
    private UserProgressDAOImpl userProgressDAO;

    @Mock
    private UserDAOImpl userDAO;

    @Mock
    private StageDAOImpl stageDAO;

    @InjectMocks
    private UserProgressServiceImpl userProgressService;

    @Test
    void saveProgress_NotExistingUser_ShouldRuntimeError() {
        UserProgressRequestDTO userprogressRequestDTO = new UserProgressRequestDTO("user_not_exist", 1);
        
        when(userDAO.findByNickname(userprogressRequestDTO.getNickname())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userProgressService.saveProgress(userprogressRequestDTO);
        });

        verify(userDAO, times(1)).findByNickname(userprogressRequestDTO.getNickname());
        assertEquals("403 FORBIDDEN \"Usuário não encontrado\"", exception.getMessage());
    }

    @Test
    @Sql("/tests.sql")
    void saveProgress_NotExistingStage_ShouldRuntimeError() {
        UserProgressRequestDTO userprogressRequestDTO = new UserProgressRequestDTO("user", 1);
        when(userDAO.findByNickname(userprogressRequestDTO.getNickname())).thenReturn(new User());
        when(stageDAO.findByNumber(userprogressRequestDTO.getCurrentStage())).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userProgressService.saveProgress(userprogressRequestDTO);
        });
        
        verify(userDAO, times(1)).findByNickname(userprogressRequestDTO.getNickname());
        verify(stageDAO, times(1)).findByNumber(userprogressRequestDTO.getCurrentStage());
        assertEquals("403 FORBIDDEN \"Fase 1 não encontrada\"", exception.getMessage());
    }

    @Test
    void saveProgress_WhenNotExistingUserProgress_ShouldCreateUserProgress() {
        UserProgressRequestDTO userprogressRequestDTO = new UserProgressRequestDTO("new_user", 1);
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setNickname("user");

        UserProgress mockProgress = new UserProgress();
        mockProgress.setUser(mockUser);
        mockProgress.setCurrentStage(10);

        Stage mockStage = new Stage();
        mockStage.setId(1);
        mockStage.setNumber(10);

        when(userDAO.findByNickname(userprogressRequestDTO.getNickname())).thenReturn(mockUser);
        when(stageDAO.findByNumber(userprogressRequestDTO.getCurrentStage())).thenReturn(mockStage);
        when(userProgressDAO.findByUserId(1)).thenReturn(null);
        doNothing().when(userProgressDAO).save(any(UserProgress.class));

        String request = userProgressService.saveProgress(userprogressRequestDTO);
        
        verify(userDAO, times(1)).findByNickname(userprogressRequestDTO.getNickname());
        verify(stageDAO, times(1)).findByNumber(userprogressRequestDTO.getCurrentStage());
        verify(userProgressDAO, times(1)).findByUserId(1);
        verify(userProgressDAO, times(1)).save(any(UserProgress.class));
        assertEquals("Progresso do usuário criado com sucesso", request);
    }

    @Test
    void saveProgress_WhenExistingUserProgress_ShouldUpdateUserProgress() {
        UserProgressRequestDTO userprogressRequestDTO = new UserProgressRequestDTO("user", 1);
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setNickname("user");

        UserProgress mockProgress = new UserProgress();
        mockProgress.setUser(mockUser);
        mockProgress.setCurrentStage(10);

        Stage mockStage = new Stage();
        mockStage.setId(1);
        mockStage.setNumber(10);

        when(userDAO.findByNickname(userprogressRequestDTO.getNickname())).thenReturn(mockUser);
        when(stageDAO.findByNumber(userprogressRequestDTO.getCurrentStage())).thenReturn(mockStage);
        when(userProgressDAO.findByUserId(1)).thenReturn(mockProgress);

        String request = userProgressService.saveProgress(userprogressRequestDTO);
        
        verify(userDAO, times(1)).findByNickname(userprogressRequestDTO.getNickname());
        verify(stageDAO, times(1)).findByNumber(userprogressRequestDTO.getCurrentStage());
        verify(userProgressDAO, times(1)).findByUserId(1);
        verify(userProgressDAO, times(0)).save(mockProgress);
        verify(userProgressDAO, times(1)).update(mockProgress);
        assertEquals("Progresso do usuário atualizado com sucesso", request);
    }

    @Test
    void getUserProgress_WhenNotExistingUser_ShouldRuntimeError() {
        when(userDAO.findByNickname("user")).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userProgressService.getUserProgress("user");
        });

        verify(userDAO, times(1)).findByNickname("user");
        assertEquals("403 FORBIDDEN \"Usuário não encontrado\"", exception.getMessage());
    }

    @Test
    void getUserProgress_WhenNotExistingUserProgress_ShouldCreateUserProgress() {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setNickname("user");

        when(userDAO.findByNickname("user")).thenReturn(mockUser);
        when(userProgressDAO.findByUserId(anyInt())).thenReturn(null);
        doNothing().when(userProgressDAO).save(any(UserProgress.class));

        UserProgressResponseDTO request = userProgressService.getUserProgress("user");
        
        verify(userDAO, times(1)).findByNickname("user");
        verify(userProgressDAO, times(1)).findByUserId(anyInt());
        verify(userProgressDAO, times(1)).save(any(UserProgress.class));
        assertThat(request).isNotNull();
    }

    @Test
    void getUserProgress_WhenExistingUser_ShouldReturnUserProgress() {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setNickname("user");

        UserProgress mockProgress = new UserProgress();
        mockProgress.setUser(mockUser);
        mockProgress.setCurrentStage(10);


        when(userDAO.findByNickname("user")).thenReturn(mockUser);
        when(userProgressDAO.findByUserId(anyInt())).thenReturn(mockProgress);
        UserProgressResponseDTO response = userProgressService.getUserProgress("user");


        verify(userDAO, times(1)).findByNickname("user");
        verify(userProgressDAO, times(1)).findByUserId(anyInt());
        verify(userProgressDAO, times(0)).save(any(UserProgress.class));
        assertThat(response).isNotNull();
    }

}
