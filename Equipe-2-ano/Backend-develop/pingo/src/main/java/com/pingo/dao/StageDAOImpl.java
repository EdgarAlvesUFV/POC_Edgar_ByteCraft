package com.pingo.dao;

import com.pingo.dao.interfaces.StageDAO;
import com.pingo.model.entity.Stage;

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
public class StageDAOImpl implements StageDAO {
    private final DataSource dataSource;

    public StageDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Stage stage) {
        String sql = "INSERT INTO Stages(name, number, created_at, updated_at, is_active) VALUES (?,?,NOW(),NOW(),1);";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Executando query sql
            pstmt.setString(1, stage.getName());
            pstmt.setInt(2, stage.getNumber());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    stage.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o estágio", e);
        }
    }

   @Override
     public List<Stage> findAll() {
        String sql = "SELECT * FROM Stages;";
        List<Stage> stages = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            // Executa query sql
            while (rs.next()) {
                if(rs.getInt("is_active") == 1) {
                    Stage stage = new Stage();
                    stage.setId(rs.getInt("id"));
                    stage.setName(rs.getString("name"));
                    stage.setNumber(rs.getInt("number"));
                    stages.add(stage);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar todos os estágios", e);
        }
        return stages;
    }

    @Override
    public Stage findById(int id) {
        String sql = "SELECT * FROM Stages WHERE id = ?;";
        Stage stage = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if(rs.getInt("is_active") == 1) {
                        stage = new Stage();
                        stage.setId(rs.getInt("id"));
                        stage.setName(rs.getString("name"));
                        stage.setNumber(rs.getInt("number"));
                        return stage;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar estágio por id", e);
        }
        return null;
    }

    @Override
    public Stage findByName(String name) {
        String sql = "SELECT * FROM Stages WHERE name = ?;";
        Stage stage = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql      
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if(rs.getInt("is_active") == 1) {
                        stage = new Stage();
                        stage.setId(rs.getInt("id"));
                        stage.setName(rs.getString("name"));
                        stage.setNumber(rs.getInt("number"));
                        return stage;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar estágio por nome", e);
        }
        return null;
    }

    @Override
    public Stage findByNumber(int number) {
        String sql = "SELECT * FROM Stages WHERE number = ?;";
        Stage stage = null;
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setInt(1, number);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if(rs.getInt("is_active") == 1) {
                        stage = new Stage();
                        stage.setId(rs.getInt("id"));
                        stage.setName(rs.getString("name"));
                        stage.setNumber(rs.getInt("number"));
                        return stage;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar estágio por número", e);
        }
        return null;
    }

    @Override
    public Stage update(Stage stage) {
        String sql = "UPDATE Stages SET name = ?, number = ?, updated_at = NOW() WHERE id = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query sql
            pstmt.setString(1, stage.getName());
            pstmt.setInt(2, stage.getNumber());
            pstmt.setInt(3, stage.getId());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // Retorna usuário atualizado
                return findById(stage.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o estágio", e);
        }
        return null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Stages WHERE id = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Executando query SQL
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar o estágio", e);
        }
    }
}