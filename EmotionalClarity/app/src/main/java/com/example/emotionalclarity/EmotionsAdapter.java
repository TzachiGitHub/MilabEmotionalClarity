package com.example.emotionalclarity;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EmotionsAdapter extends RecyclerView.Adapter {
    private ArrayList<Emotion> mDataSet;

    public EmotionsAdapter(ArrayList<Emotion> emotions){
        mDataSet = emotions;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public MyViewHolder(View view) {
            super(view);
            mTextView = view.findViewById(R.id.emotionName);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emotion_cell,
                parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Emotion thisEmotion = mDataSet.get(position);
        ((MyViewHolder)holder).mTextView.setText(thisEmotion.name);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
