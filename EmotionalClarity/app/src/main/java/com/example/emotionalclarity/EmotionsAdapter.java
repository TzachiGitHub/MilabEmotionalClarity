package com.example.emotionalclarity;


import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private OnListChangeListener mListener;

    public EmotionsAdapter(ArrayList<EmotionResponse> emotions, OnListChangeListener listener) {
        mDataSet = emotions;
        mListener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;
        private LinearLayout mEmotionCell;
        private SeekBar mSeekBar;
        private ImageButton mTrashButton;

        public MyViewHolder(View view) {
            super(view);
            mTextView = view.findViewById(R.id.emotionName);
            mEmotionCell = view.findViewById(R.id.emotionCell);
            mSeekBar = view.findViewById(R.id.seekBar);
            mTrashButton = view.findViewById(R.id.trashButton);
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
        final String name = thisEmotion.getName();
        int score = (int)(thisEmotion.getScore() * 10);
        Tone tone = new EmotionMap().map(name);
        int color = tone.getColor();
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(20);
        gradientDrawable.setColor(color);
        myViewHolder.mEmotionCell.setBackground(gradientDrawable);

        // Display the name of the emotion:
        myViewHolder.mTextView.setText(name);
        // Set the seek-bar to the emotion's score (ranges from 0 to 10 inclusive):
        myViewHolder.mSeekBar.setMax(10);
        myViewHolder.mSeekBar.setProgress(score);
        // Delete cell:
        myViewHolder.mTrashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newPosition = myViewHolder.getAdapterPosition();
                mDataSet.remove(newPosition);
                notifyItemRemoved(newPosition);
                notifyItemRangeChanged(newPosition, mDataSet.size());
                mListener.onListChange(name);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public interface OnListChangeListener {
        void onListChange(String name);
    }

}
