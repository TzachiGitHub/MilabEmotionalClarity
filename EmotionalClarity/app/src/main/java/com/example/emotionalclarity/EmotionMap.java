package com.example.emotionalclarity;
import java.util.HashMap;
// Emotion wrapper class
// Used to map emotion responses to emotions

public class EmotionMap {
    private HashMap<String, Tone> emotionMap = new HashMap<>();

    public EmotionMap(){
        emotionMap.put("Happiness", new Tone(new String[]{"Joy", "Optimism", "Delight", "Cheerfulness",
                "Euphoria"}, 0xffef7ef0, 0xfff40df6));
        emotionMap.put("Excitement", new Tone(new String[]{"Enthusiasm", "Passion", "Craving"},
                0xfff3f484, 0xffe5e70c));
        emotionMap.put("Anger", new Tone(new String[]{"Grudge", "Envy", "Resentment", "Aggressiveness",
                "Bitterness", "Vindictiveness"}, 0xffee7e78, 0xfff01e13));
        emotionMap.put("Sadness", new Tone(new String[]{"Sorrow", "Loneliness", "Self-pity", "Betrayed",
                "Dismay", "Desperation"}, 0xffafe5ea, 0xff1a47ec));
        emotionMap.put("Fear", new Tone(new String[]{"Anxiety", "Worry", "Nervousness", "Uncertainty"},
                0xffbbbdc3, 0xff6d6d6f));
        emotionMap.put("Insecurity", new Tone(new String[]{"Doubt", "Confusion"},
                0xff9de5a1, 0xff16ec51));
        emotionMap.put("Confidence", new Tone(new String[]{"Control", "Determination"},
                0xffeecda4, 0xfff49319));
        emotionMap.put("Boredom", new Tone(new String[]{"Apathy", "Indifference", "Obtuseness"},
                0xffd29bf9, 0xff992be7));
    }
    public Tone map(String emotionName){
        return this.emotionMap.get(emotionName);
    }
}
