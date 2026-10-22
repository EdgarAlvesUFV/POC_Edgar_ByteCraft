package com.pingo.dao.interfaces;
import java.util.List;

import com.pingo.model.entity.UserProgress;

public interface UserProgressDAO {

    public void save(UserProgress userProgress);

    public List<UserProgress> findAll();

    public UserProgress findByUserId(int userId);

    public UserProgress update(UserProgress userProgress);

    public void delete(int id);
}
