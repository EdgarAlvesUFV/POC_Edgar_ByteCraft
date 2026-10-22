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

import com.pingo.dao.interfaces.UserDAO;
import com.pingo.model.entity.User;

@Repository
public class UserDAOImpl implements UserDAO {
    private final DataSource dataSource;

    public UserDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO Users(nickname, password, created_at, updated_at, is_active) VALUES (?, ?, NOW(), NOW(), 1);";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Executando query sql
            pstmt.setString(1, user.getNickname());
            pstmt.setString(2, user.getPassword());
            pstmt.executeUpdate();

            /// Recupera as chaves (IDs) que o banco gerou automaticamente após o INSERT
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM Users;";
        List<User> users = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executa query sql
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Verifica se o usuário está ativo
                if(rs.getInt("is_active") == 1) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setNickname(rs.getString("nickname"));
                    user.setPassword(rs.getString("password"));
                    users.add(user); // Adiciona usuário na lista
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM Users WHERE id = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Verifica se o usuário está ativo
                    if(rs.getInt("is_active") == 1) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setNickname(rs.getString("nickname"));
                        user.setPassword(rs.getString("password"));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public User findByNickname(String nickname) {
        String sql = "SELECT * FROM Users WHERE nickname = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setString(1, nickname);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Verifica se o usuário está ativo
                    if(rs.getInt("is_active") == 1) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setNickname(rs.getString("nickname"));
                        user.setPassword(rs.getString("password"));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE Users SET nickname = ?, password = ?, updated_at = NOW() WHERE id = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setString(1, user.getNickname());
            pstmt.setString(2, user.getPassword());
            pstmt.setInt(3, user.getId());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // Retorna usuário atualizado
                return findById(user.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void delete(int id) {
        String sql = "UPDATE Users SET is_active = 0, updated_at = NOW() WHERE id = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query SQL
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
}
