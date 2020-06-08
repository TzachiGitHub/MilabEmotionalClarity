package com.example.emotionalclarity;

/**
 * Object to represent emotions that were returned from the server
 * Name field - name of the emotion
 * Score field - score of the emotion (0 < score < 1)
 */
public class EmotionResponse {
    private String name;
    private double score;

    public EmotionResponse(String name, double score){
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public double getScore() {
        return score;
    }
}
