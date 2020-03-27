package com.example.emotionalclarity;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        private Button mButton;

        public MyViewHolder(View view) {
            super(view);
            mTextView = view.findViewById(R.id.emotionName);
            mButton = view.findViewById(R.id.infoButton);
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
        final Emotion thisEmotion = mDataSet.get(position);
        ((MyViewHolder)holder).mTextView.setText(thisEmotion.name);
        ((MyViewHolder)holder).mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EmotionInfoActivity.class);
                intent.putExtra("NAME", thisEmotion.name);
                intent.putExtra("DEFINITION", thisEmotion.definition);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
