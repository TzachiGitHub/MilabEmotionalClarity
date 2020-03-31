package com.example.emotionalclarity;

// Emotion object

import android.os.Parcel;
import android.os.Parcelable;

public class Emotion implements Parcelable {
    String name;
    String definition;

    public Emotion(String name, String definition){
        this.name = name;
        this.definition = definition;
    }

    protected Emotion(Parcel in) {
        name = in.readString();
        definition = in.readString();
    }

    public static final Creator<Emotion> CREATOR = new Creator<Emotion>() {
        @Override
        public Emotion createFromParcel(Parcel in) {
            return new Emotion(in);
        }

        @Override
        public Emotion[] newArray(int size) {
            return new Emotion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(definition);
    }

    public String toString(){
        return this.name;
    }
}
