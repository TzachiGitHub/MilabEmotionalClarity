package com.example.emotionalclarity;

public class Tone {
    String[] emotions;
    int[] colors;
    public Tone(String[] emotions, int startColor, int endColor) {
        this.emotions = emotions;
        this.colors = new int[]{startColor, endColor};
    }

    public int getStartColor() {
        return colors[0];
    }
    public int getEndColor() {
        return colors[1];
    }
}
