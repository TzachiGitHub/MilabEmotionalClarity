package com.example.emotionalclarity;

/**
 * This class represents button positions in the main screen
 * marginTop, marginStart: margins of the buttons from top and start of the screen respectively.
 */
public class ButtonPositioning {
    private int marginTop;
    private int marginStart;

    public ButtonPositioning(int marginTop, int marginStart){
        this.marginTop = marginTop;
        this.marginStart = marginStart;
    }

    public int getMarginTop() {return this.marginTop;}
    public int getMarginStart() {return this.marginStart;}
}
