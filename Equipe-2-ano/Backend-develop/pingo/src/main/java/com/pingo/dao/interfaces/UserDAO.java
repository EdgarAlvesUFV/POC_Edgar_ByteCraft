package com.pingo.dao.interfaces;

import java.util.List;

import com.pingo.model.entity.User;

public interface UserDAO {

    void save(User user);

    List<User> findAll();

    User findById(int id);

    User findByNickname(String nickname);

    User update(User user);

    void delete(int id);
    
}
