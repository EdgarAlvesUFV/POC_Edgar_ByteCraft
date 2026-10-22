package com.pingo.dao.interfaces;

import java.util.List;

import com.pingo.model.entity.Score;

public interface ScoreDAO {
    void save(Score score);

    List<Score> findAll();

    Score findById(int scoreId);

    List<Score> findByUserId(int userId);

    List<Score> findByStageId(int stageId);

    Score findByUserIdAndStageId(int userId, int stageId);

    Score update(Score score);

    void delete(int scoreId);
}
