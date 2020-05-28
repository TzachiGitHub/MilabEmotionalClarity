package com.example.emotionalclarity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class EmotionsAdapter extends RecyclerView.Adapter {
    private ArrayList<EmotionResponse> mDataSet;
    private ArrayList<EmotionResponse> checkedList; // List of emotions that the user checked

    public EmotionsAdapter(ArrayList<EmotionResponse> emotions) {
        mDataSet = emotions;
        checkedList = new ArrayList<>();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;
        private LinearLayout mEmotionCell;
        private SeekBar mSeekBar;
        private ImageButton mTrashButton;
        //final private TextView mScore;

        public MyViewHolder(View view) {
            super(view);
            mTextView = view.findViewById(R.id.emotionName);
            mEmotionCell = view.findViewById(R.id.emotionCell);
            mSeekBar = view.findViewById(R.id.seekBar);
            mTrashButton = view.findViewById(R.id.trashButton);
            //mScore = view.findViewById(R.id.score);
        }

        public void getItem() {

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder myViewHolder = (MyViewHolder)holder;
        final EmotionResponse thisEmotion = mDataSet.get(position);
        String name = thisEmotion.getName();
        int score = (int)(thisEmotion.getScore() * 10);
        Tone tone = new EmotionMap().map(name);
        int color = tone.getStartColor();
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(20);
        gradientDrawable.setColor(color);
        myViewHolder.mEmotionCell.setBackground(gradientDrawable);

        // Display the name of the emotion:
        myViewHolder.mTextView.setText(name);
        // Set the seek-bar to the emotion's score (ranges from 0 to 10 inclusive):
        myViewHolder.mSeekBar.setMax(10);
        myViewHolder.mSeekBar.setProgress(score);
        /*
        if (score < 10) {
            myViewHolder.mScore.setText("  " + score);
        } else {
            myViewHolder.mScore.setText("" + score);
        }
        myViewHolder.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 10) {
                    myViewHolder.mScore.setText("  " + progress);
                } else {
                    myViewHolder.mScore.setText("" + progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        */

        // Delete cell:
        myViewHolder.mTrashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newPosition = myViewHolder.getAdapterPosition();
                mDataSet.remove(newPosition);
                notifyItemRemoved(newPosition);
                notifyItemRangeChanged(newPosition, mDataSet.size());
            }
        });

    }

    /**
     *
     * @return The list of emotions that the user checked,
     * which are the emotions that we need to add
     */
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public ArrayList<EmotionResponse> getCheckedList(){
        return checkedList;
    }
}
