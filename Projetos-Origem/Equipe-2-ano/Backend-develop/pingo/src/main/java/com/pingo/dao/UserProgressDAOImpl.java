package com.pingo.dao;

import com.pingo.dao.interfaces.UserProgressDAO;
import com.pingo.model.entity.User;
import com.pingo.model.entity.UserProgress;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

@Repository
public class UserProgressDAOImpl implements UserProgressDAO {
    private final DataSource dataSource;

    public UserProgressDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(UserProgress userProgress) {
        String sql = "INSERT INTO `UserProgress`(user_id, current_stage, created_at, updated_at, is_active) VALUES (?,?,NOW(),NOW(),1);";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userProgress.getUser().getId());
            pstmt.setInt(2, userProgress.getCurrentStage());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    userProgress.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o progresso do usuário", e);
        }
    }

    @Override
    public List<UserProgress> findAll() {
        String sql = "SELECT * FROM `UserProgress`";
        List<UserProgress> usersProgress = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("user_id"));

                if(rs.getInt("is_active") == 1) {
                    String userSql = "SELECT * FROM Users WHERE id = ?";
                    try (PreparedStatement userPstmt = conn.prepareStatement(userSql)) {
                        userPstmt.setInt(1, user.getId());
                        try (ResultSet userRs = userPstmt.executeQuery()) {
                            if (userRs.next()) {
                                if(userRs.getInt("is_active") == 1) {
                                    user.setNickname(userRs.getString("nickname"));
                                }
                            }
                        }
                    }

                    UserProgress userProgress = new UserProgress();
                    userProgress.setId(rs.getInt("id"));
                    userProgress.setCurrentStage(rs.getInt("current_stage"));
                    userProgress.setUser(user);
                    usersProgress.add(userProgress);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ver o progresso de todos os usuários", e);
        }
        return usersProgress;
    }

    @Override
    public UserProgress findByUserId(int userId) {
        String sql = "SELECT * FROM `UserProgress` WHERE user_id = ?";
        UserProgress userProgress = null;

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if(rs.getInt("is_active") == 1) {
                        userProgress = new UserProgress();
                        userProgress.setId(rs.getInt("id"));
                        userProgress.setCurrentStage(rs.getInt("current_stage"));

                        User user = new User();
                        user.setId(userId);

                        String userSql = "SELECT * FROM Users WHERE id = ?";
                        try (PreparedStatement userPstmt = conn.prepareStatement(userSql)) {
                            userPstmt.setInt(1, userId);
                            try (ResultSet userRs = userPstmt.executeQuery()) {
                                if (userRs.next()) {
                                    if(userRs.getInt("is_active") == 1) {
                                        user.setNickname(userRs.getString("nickname"));
                                    }
                                }
                            }
                        }
                        userProgress.setUser(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ver o progresso de um usuário", e);
        }
        return userProgress;
    }

    @Override
    public UserProgress update(UserProgress userProgress) {
        String sql = "UPDATE `UserProgress` SET current_stage = ?, updated_at = NOW() WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userProgress.getCurrentStage());
            pstmt.setInt(2, userProgress.getId());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                return findByUserId(userProgress.getUser().getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o progresso de um usuário", e);
        }
        return null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM `UserProgress` WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar o progresso de um usuário", e);
        }
    }
}