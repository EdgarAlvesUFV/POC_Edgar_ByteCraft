package com.pingo.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.pingo.dao.interfaces.DifficultDAO;
import com.pingo.model.entity.Difficult;

@Repository
public class DifficultDAOImpl implements DifficultDAO {
    
    public void save(Difficult difficult) {
        String sql = "INSERT INTO Difficulties(noise_amount, light_amount, speed, level, created_at, updated_at, is_active) VALUES(?,?,?,?,NOW(),NOW(),1);";
        
    }

    public List<Difficult> findAll() {
        String sql = "SELECT * FROM Difficulties;";
        return null;
    }

    public Difficult findById(int id) {
        String sql = "SELECT * FROM Difficulties WHERE id = ?;";
        return null;
    }

    public Difficult update(Difficult difficult) {
        String sql = "UPDATE Difficulties SET noise_amount = ?, light_amount = ?, speed = ?, level = ?, updated_at = NOW() WHERE id = ?;";
        return null;
    }   

    public void delete(int id) {
        String sql = "DELETE FROM Difficulties WHERE id = ?;";
    }
}
