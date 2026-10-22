package com.pingo.service.interfaces;

import com.pingo.model.dto.ScoreCreateRequestDTO;
import com.pingo.model.dto.ScoreResponseDTO;
import com.pingo.model.dto.ScoreUpdateRequestDTO;
import com.pingo.model.dto.TotalScoreResponseDTO;
import com.pingo.model.entity.Score;

public interface ScoreService {
    
    public ScoreResponseDTO createScore(ScoreCreateRequestDTO request);

    public Score addPointsToScore(int userId, int stageId, int points);

    public ScoreResponseDTO getScoreByUserAndStage(int userId, int stageId);

    public ScoreResponseDTO updateScore(ScoreUpdateRequestDTO request);

    public TotalScoreResponseDTO getTotalScoreByUserId(int userId);
}
