package com.pingo.model.entity;

public class UserProgress {
    private int id;
    private User user;
    private int currentStage;
    
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public int getCurrentStage(){
        return currentStage;
    }

    public void setCurrentStage(int currentStage){
        this.currentStage = currentStage;
    }

}

