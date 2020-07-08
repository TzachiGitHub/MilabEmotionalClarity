package com.feelyou.emotional_clarity;

public class User {
    private String name;
    private String gender;

    public User(){
        // Empty constructor
    }

    User(String name, String gender){
        this.name = name;
        this.gender = gender;
    }


    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getGender(){
        return this.gender;
    }

    public void setGender(String gender){
        this.gender = gender;
    }

    public String toString(){
        return this.name + " (" + this.gender + ")";
    }
}
