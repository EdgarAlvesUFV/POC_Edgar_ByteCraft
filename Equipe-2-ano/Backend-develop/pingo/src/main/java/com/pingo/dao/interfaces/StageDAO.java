package com.pingo.dao.interfaces;

import java.util.List;
import com.pingo.model.entity.Stage;

public interface StageDAO {

    public void save(Stage stage);

    public List<Stage> findAll();

    public Stage findById(int id);

    public Stage findByName(String name);

    public Stage findByNumber(int number);

    public Stage update(Stage stage);

    public void delete(int id);
}
