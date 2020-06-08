package com.example.emotionalclarity;
import java.util.ArrayList;
import java.util.HashMap;

// Used to map Emotions to Tones

public class EmotionMap {
    private HashMap<String, Tone> emotionMap = new HashMap<>();

    public EmotionMap(){
        emotionMap.put("Happiness", new Tone(new String[]{"Joy", "Optimism", "Delight", "Cheerfulness",
                "Euphoria"}, 0xffef7ef0));
        emotionMap.put("Excitement", new Tone(new String[]{"Enthusiasm", "Passion", "Craving"},
                0xfff3f484));
        emotionMap.put("Anger", new Tone(new String[]{"Grudge", "Envy", "Resentment", "Aggressiveness",
                "Bitterness", "Vindictiveness"}, 0xffee7e78));
        emotionMap.put("Sadness", new Tone(new String[]{"Sorrow", "Loneliness", "Self-pity", "Betrayed",
                "Dismay", "Desperation"}, 0xffafe5ea));
        emotionMap.put("Fear", new Tone(new String[]{"Anxiety", "Worry", "Nervousness", "Uncertainty"},
                0xffbbbdc3));
        emotionMap.put("Insecurity", new Tone(new String[]{"Doubt", "Confusion"},
                0xff9de5a1));
        emotionMap.put("Confidence", new Tone(new String[]{"Control", "Determination"},
                0xffeecda4));
        emotionMap.put("Boredom", new Tone(new String[]{"Apathy", "Indifference", "Obtuseness"},
                0xffd29bf9));
    }
    public Tone map(String emotionName){
        return this.emotionMap.get(emotionName);
    }

    public ArrayList<String> getKeys(){
        return new ArrayList<>(emotionMap.keySet());
    }
}
