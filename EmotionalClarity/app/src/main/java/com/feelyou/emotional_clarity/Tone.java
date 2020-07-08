package com.feelyou.emotional_clarity;

public class Tone {
    private String[] emotions;
    private int color;
    public Tone(String[] emotions, int color) {
        this.emotions = emotions;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

}
