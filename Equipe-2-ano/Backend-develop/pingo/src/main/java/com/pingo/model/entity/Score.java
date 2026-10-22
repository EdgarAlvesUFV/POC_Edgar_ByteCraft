package com.pingo.model.entity;

public class Score {
    private int id;
    private int points;
    private Stage stage;
    private User user;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getPoints(){
        return points;
    }

    public void setPoints(int points){
        this.points = points;
    }

    public Stage getStage(){
        return stage;
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public void addPoints(int points){
        this.points += points;
    }

    public void resetPoints(){
        this.points = 0;
    }

}
