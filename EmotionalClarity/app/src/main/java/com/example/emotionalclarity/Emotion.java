package com.example.emotionalclarity;

// Emotion object

public class Emotion {
    String name;
    String definition;

    public Emotion(String name, String definition){
        this.name = name;
        this.definition = definition;
    }

    public String toString(){
        return this.name;
    }
}
