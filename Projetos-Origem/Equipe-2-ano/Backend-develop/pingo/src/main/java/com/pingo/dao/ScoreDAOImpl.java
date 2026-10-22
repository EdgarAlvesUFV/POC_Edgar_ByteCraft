package com.pingo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.pingo.dao.interfaces.ScoreDAO;
import com.pingo.model.entity.Score;
import com.pingo.model.entity.Stage;
import com.pingo.model.entity.User;

@Repository
public class ScoreDAOImpl implements ScoreDAO {
    private final DataSource dataSource;

    public ScoreDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Score score) {
        String sql = "INSERT INTO Scores(points, stage_id, user_id, created_at, updated_at, is_active) VALUES (?,?,?,NOW(),NOW(),1);";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Executando query sql
            pstmt.setInt(1, score.getPoints());
            pstmt.setInt(2, score.getStage().getId());
            pstmt.setInt(3, score.getUser().getId());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    score.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o score", e);
        }
    }

    @Override
    public List<Score> findAll() {
        String sql = "SELECT * FROM Scores;";
        List<Score> scores = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            // Executa query sql
            while (rs.next()) {
                if(rs.getInt("is_active") == 1) {
                    Score score = new Score();
                    score.setId(rs.getInt("id"));
                    score.setPoints(rs.getInt("points"));

                    User user = new User();
                    String userSql = "SELECT * FROM Users WHERE id = ?;";
                    try (PreparedStatement userPstmt = conn.prepareStatement(userSql)) {
                        userPstmt.setInt(1, rs.getInt("id"));
                        try (ResultSet userRs = userPstmt.executeQuery()) {
                            if (userRs.next()) {
                                if(userRs.getInt("is_active") == 1) {
                                    user.setId(userRs.getInt("id"));
                                    user.setNickname(userRs.getString("nickname"));
                                    user.setPassword(userRs.getString("password"));
                                }
                            }
                        }
                    }
                    score.setUser(user);

                    Stage stage = new Stage();
                    String stageSql = "SELECT * FROM Stages WHERE id = ?;";
                    try (PreparedStatement stagePstmt = conn.prepareStatement(stageSql)) {
                        stagePstmt.setInt(1, rs.getInt("id"));
                        try (ResultSet stageRs = stagePstmt.executeQuery()) {
                            if (stageRs.next()) {
                                if(stageRs.getInt("is_active") == 1) {
                                    stage.setId(stageRs.getInt("id"));
                                    stage.setName(stageRs.getString("name"));
                                    stage.setNumber(stageRs.getInt("number"));
                                }
                            }
                        }
                    }
                    score.setStage(stage);

                    scores.add(score);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar todos os scores", e);
        }
        return scores;
    }

    @Override
    public Score findById(int scoreId) {
        String sql = "SELECT * FROM Scores WHERE id = ?;";
        Score score = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setInt(1, scoreId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if(rs.getInt("is_active") == 1) {
                        score = new Score();
                        score.setId(rs.getInt("id"));
                        score.setPoints(rs.getInt("points"));
                        User user = new User();
                        String userSql = "SELECT * FROM Users WHERE id = ?;";
                        try (PreparedStatement userPstmt = conn.prepareStatement(userSql)) {
                            userPstmt.setInt(1, rs.getInt("id"));
                            try (ResultSet userRs = userPstmt.executeQuery()) {
                                if (userRs.next()) {
                                    if(userRs.getInt("is_active") == 1) {
                                        user.setId(userRs.getInt("id"));
                                        user.setNickname(userRs.getString("nickname"));
                                        user.setPassword(userRs.getString("password"));
                                    }
                                }
                            }
                        }
                        score.setUser(user);

                        Stage stage = new Stage();
                        String stageSql = "SELECT * FROM Stages WHERE id = ?;";
                        try (PreparedStatement stagePstmt = conn.prepareStatement(stageSql)) {
                            stagePstmt.setInt(1, rs.getInt("id"));
                            try (ResultSet stageRs = stagePstmt.executeQuery()) {
                                if (stageRs.next()) {
                                    if(stageRs.getInt("is_active") == 1) {
                                        stage.setId(stageRs.getInt("id"));
                                        stage.setName(stageRs.getString("name"));
                                        stage.setNumber(stageRs.getInt("number"));
                                    }
                                }
                            }
                        }
                        score.setStage(stage);

                        return score;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar score por id", e);
        }
        return null;
    }

    @Override
    public List<Score> findByUserId(int userId) {
        String sql = "SELECT * FROM Scores WHERE user_id = ?;";
        List<Score> scores = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if(rs.getInt("is_active") == 1) {
                        Score score = new Score();
                        score.setId(rs.getInt("id"));
                        score.setPoints(rs.getInt("points"));
                        User user = new User();
                        String userSql = "SELECT * FROM Users WHERE id = ?;";
                        try (PreparedStatement userPstmt = conn.prepareStatement(userSql)) {
                            userPstmt.setInt(1, rs.getInt("id"));
                            try (ResultSet userRs = userPstmt.executeQuery()) {
                                if (userRs.next()) {
                                    if(userRs.getInt("is_active") == 1) {
                                        user.setId(userRs.getInt("id"));
                                        user.setNickname(userRs.getString("nickname"));
                                        user.setPassword(userRs.getString("password"));
                                    }
                                }
                            }
                        }
                        score.setUser(user);

                        Stage stage = new Stage();
                        String stageSql = "SELECT * FROM Stages WHERE id = ?;";
                        try (PreparedStatement stagePstmt = conn.prepareStatement(stageSql)) {
                            stagePstmt.setInt(1, rs.getInt("id"));
                            try (ResultSet stageRs = stagePstmt.executeQuery()) {
                                if (stageRs.next()) {
                                    if(stageRs.getInt("is_active") == 1) {
                                        stage.setId(stageRs.getInt("id"));
                                        stage.setName(stageRs.getString("name"));
                                        stage.setNumber(stageRs.getInt("number"));
                                    }
                                }
                            }
                        }
                        score.setStage(stage);
                        scores.add(score);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar scores por user_id", e);
        }
        return scores;
    }

    @Override
    public List<Score> findByStageId(int stageId) {
        String sql = "SELECT * FROM Scores WHERE stage_id = ?;";
        List<Score> scores = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setInt(1, stageId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if(rs.getInt("is_active") == 1) {
                        Score score = new Score();
                        score.setId(rs.getInt("id"));
                        score.setPoints(rs.getInt("points"));
                        User user = new User();
                        String userSql = "SELECT * FROM Users WHERE id = ?;";
                        try (PreparedStatement userPstmt = conn.prepareStatement(userSql)) {
                            userPstmt.setInt(1, rs.getInt("id"));
                            try (ResultSet userRs = userPstmt.executeQuery()) {
                                if (userRs.next()) {
                                    if(userRs.getInt("is_active") == 1) {
                                        user.setId(userRs.getInt("id"));
                                        user.setNickname(userRs.getString("nickname"));
                                        user.setPassword(userRs.getString("password"));
                                    }
                                }
                            }
                        }
                        score.setUser(user);

                        Stage stage = new Stage();
                        String stageSql = "SELECT * FROM Stages WHERE id = ?;";
                        try (PreparedStatement stagePstmt = conn.prepareStatement(stageSql)) {
                            stagePstmt.setInt(1, rs.getInt("id"));
                            try (ResultSet stageRs = stagePstmt.executeQuery()) {
                                if (stageRs.next()) {
                                    if(stageRs.getInt("is_active") == 1) {
                                        stage.setId(stageRs.getInt("id"));
                                        stage.setName(stageRs.getString("name"));
                                        stage.setNumber(stageRs.getInt("number"));
                                    }
                                }
                            }
                        }
                        score.setStage(stage);
                        scores.add(score);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar scores por stage_id", e);
        }
        return scores;
    }

    @Override
    public Score findByUserIdAndStageId(int userId, int stageId) {
       String sql = "SELECT * FROM Scores WHERE user_id = ? AND stage_id = ?;";
       Score score = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setInt(1, userId);
            pstmt.setInt(2, stageId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if(rs.getInt("is_active") == 1) {
                        score = new Score();
                        score.setId(rs.getInt("id"));
                        score.setPoints(rs.getInt("points"));
                        User user = new User();
                        String userSql = "SELECT * FROM Users WHERE id = ?;";
                        try (PreparedStatement userPstmt = conn.prepareStatement(userSql)) {
                            userPstmt.setInt(1, rs.getInt("id"));
                            try (ResultSet userRs = userPstmt.executeQuery()) {
                                if (userRs.next()) {
                                    if(userRs.getInt("is_active") == 1) {
                                        user.setId(userRs.getInt("id"));
                                        user.setNickname(userRs.getString("nickname"));
                                        user.setPassword(userRs.getString("password"));
                                    }
                                }
                            }
                        }
                        score.setUser(user);

                        Stage stage = new Stage();
                        String stageSql = "SELECT * FROM Stages WHERE id = ?;";
                        try (PreparedStatement stagePstmt = conn.prepareStatement(stageSql)) {
                            stagePstmt.setInt(1, rs.getInt("id"));
                            try (ResultSet stageRs = stagePstmt.executeQuery()) {
                                if (stageRs.next()) {
                                    if(stageRs.getInt("is_active") == 1) {
                                        stage.setId(stageRs.getInt("id"));
                                        stage.setName(stageRs.getString("name"));
                                        stage.setNumber(stageRs.getInt("number"));
                                    }
                                }
                            }
                        }
                        score.setStage(stage);

                        return score;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar score por userId e stageId", e);
        }
        return null;
    }

    @Override
    public Score update(Score score) {
        String sql = "UPDATE Scores SET points = ?, stage_id = ?, user_id = ?, updated_at = NOW() WHERE id = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setInt(1, score.getPoints());
            pstmt.setInt(2, score.getStage().getId());
            pstmt.setInt(3, score.getUser().getId());
            pstmt.setInt(4, score.getId());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // Retorna usuário atualizado
                return findById(score.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o score", e);
        }
        return null;
    }

    @Override
    public void delete(int scoreId) {
        String sql = "DELETE FROM Scores WHERE id = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query SQL
            pstmt.setInt(1, scoreId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar o score", e);
        }
    }
    
}