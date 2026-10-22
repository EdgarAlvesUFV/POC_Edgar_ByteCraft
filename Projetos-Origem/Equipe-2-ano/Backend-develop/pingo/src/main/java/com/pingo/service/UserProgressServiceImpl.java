package com.pingo.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pingo.dao.interfaces.StageDAO;
import com.pingo.dao.interfaces.UserDAO;
import com.pingo.dao.interfaces.UserProgressDAO;
import com.pingo.model.dto.UserProgressResponseDTO;
import com.pingo.model.dto.UserProgressRequestDTO;
import com.pingo.model.entity.Stage;
import com.pingo.model.entity.User;
import com.pingo.model.entity.UserProgress;
import com.pingo.service.interfaces.UserProgressService;

@Service
public class UserProgressServiceImpl implements UserProgressService {
    private final UserDAO userDAO;
    private final UserProgressDAO userProgressDAO;
    private final StageDAO stageDAO;

    public UserProgressServiceImpl(UserDAO userDAO, UserProgressDAO userProgressDAO, StageDAO stageDAO) {   
        this.userDAO = userDAO;
        this.userProgressDAO = userProgressDAO;
        this.stageDAO = stageDAO;
    }

    @Override
    public String saveProgress(UserProgressRequestDTO request) {
        User user = userDAO.findByNickname(request.getNickname());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não encontrado");
        }
        Stage stage = stageDAO.findByNumber(request.getCurrentStage());
        if (stage == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Fase "+ request.getCurrentStage() + " não encontrada");
        }

        UserProgress userProgress = userProgressDAO.findByUserId(user.getId());
        if (userProgress == null) {
            userProgress = new UserProgress();
            userProgress.setUser(user);
            userProgress.setCurrentStage(1);
            userProgressDAO.save(userProgress);
            return "Progresso do usuário criado com sucesso";
        }
        userProgress.setCurrentStage(request.getCurrentStage());
        userProgressDAO.update(userProgress);
        return "Progresso do usuário atualizado com sucesso";
    }

    @Override
    public UserProgressResponseDTO getUserProgress(String nickname) {
        User user = userDAO.findByNickname(nickname);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não encontrado");
        }
        UserProgress userProgress = userProgressDAO.findByUserId(user.getId());
        if (userProgress == null) {
            userProgress = new UserProgress();
            userProgress.setUser(user);
            userProgress.setCurrentStage(1);
            userProgressDAO.save(userProgress);
        }
        return new UserProgressResponseDTO(user.getNickname(), userProgress.getCurrentStage());
    }
    
}
