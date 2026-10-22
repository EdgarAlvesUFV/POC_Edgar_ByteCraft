package com.pingo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pingo.dao.interfaces.ScoreDAO;
import com.pingo.dao.interfaces.StageDAO;
import com.pingo.dao.interfaces.UserDAO;
import com.pingo.model.dto.ScoreCreateRequestDTO;
import com.pingo.model.dto.ScoreResponseDTO;
import com.pingo.model.dto.ScoreUpdateRequestDTO;
import com.pingo.model.dto.TotalScoreResponseDTO;
import com.pingo.model.entity.Score;
import com.pingo.model.entity.Stage;
import com.pingo.model.entity.User;
import com.pingo.service.interfaces.ScoreService;

@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreDAO scoreDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired 
    private StageDAO stageDAO;

    public ScoreResponseDTO createScore(ScoreCreateRequestDTO request) {
        User user = userDAO.findById(request.getUserId());
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não encontrado");
        }

        Stage stage = stageDAO.findById(request.getStageId());
        if(stage == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Fase não encontrada");
        }

        Score score = new Score();
        score.resetPoints();
        score.setStage(stage);
        score.setUser(user);
        scoreDAO.save(score);

        return new ScoreResponseDTO(score.getId(), score.getPoints(), stage.getId(), user.getId());
    }

    public Score addPointsToScore(int userId, int stageId, int points) {
        Score score = scoreDAO.findByUserIdAndStageId(userId, stageId);
        score.setPoints(score.getPoints() + points);
        scoreDAO.save(score);
        return score;
    }

    public ScoreResponseDTO getScoreByUserAndStage(int userId, int stageId) {
        User user = userDAO.findById(userId);
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não encontrado");
        }

        Stage stage = stageDAO.findById(stageId);
        if(stage == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Fase não encontrada");
        }

        Score score = scoreDAO.findByUserIdAndStageId(userId, stageId);
        if (score == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Score não encontrado");
        }
        return new ScoreResponseDTO(score.getId(), score.getPoints(), score.getStage().getId(), score.getUser().getId());
    }

    public ScoreResponseDTO updateScore(ScoreUpdateRequestDTO request) {
        User user = userDAO.findById(request.getUserId());
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não encontrado");
        }

        Stage stage = stageDAO.findById(request.getStageId());
        if(stage == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Fase não encontrada");
        }

        Score score = scoreDAO.findByUserIdAndStageId(request.getUserId(), request.getStageId());
        if (score == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Score não encontrado");
        }
        score.setPoints(request.getPoints());
        score.setStage(stage);
        score.setUser(user);
        scoreDAO.update(score);
        return new ScoreResponseDTO(score.getId(), score.getPoints(), score.getStage().getId(), score.getUser().getId());
    }

    public TotalScoreResponseDTO getTotalScoreByUserId(int userId) {
        List<Score> scores = scoreDAO.findByUserId(userId);
        int totalPoints = 0;
        for (Score score : scores) {
            totalPoints += score.getPoints();
        }
        return new TotalScoreResponseDTO(userId, totalPoints);
    }
}
