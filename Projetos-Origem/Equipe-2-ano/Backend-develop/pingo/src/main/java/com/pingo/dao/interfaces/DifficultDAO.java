package com.pingo.dao.interfaces;

import java.util.List;

import com.pingo.model.entity.Difficult;

public interface DifficultDAO {

    void save(Difficult difficult);

    List<Difficult> findAll();

    Difficult findById(int id);

    Difficult update(Difficult difficult);

    void delete(int id);

}
