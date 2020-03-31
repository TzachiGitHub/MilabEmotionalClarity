package com.example.emotionalclarity;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class EmotionsAdapter extends RecyclerView.Adapter {
    private ArrayList<Emotion> mDataSet;
    private ArrayList<Emotion> checkedList; // List of emotions that the user checked

    public EmotionsAdapter(ArrayList<Emotion> emotions) {
        mDataSet = emotions;
        checkedList = new ArrayList<>();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;
        private Button mButton;
        private CheckBox mCheckBox;

        public MyViewHolder(View view) {
            super(view);
            mTextView = view.findViewById(R.id.emotionName);
            mButton = view.findViewById(R.id.infoButton);
            mCheckBox = view.findViewById(R.id.checkEmotion);
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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Emotion thisEmotion = mDataSet.get(position);
        final int index = position;
        ((MyViewHolder)holder).mTextView.setText(thisEmotion.name);

        // Info button
        ((MyViewHolder)holder).mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EmotionInfoActivity.class);
                intent.putExtra("NAME", thisEmotion.name);
                intent.putExtra("DEFINITION", thisEmotion.definition);
                v.getContext().startActivity(intent);
            }
        });

        // Check/uncheck emotion
        ((MyViewHolder)holder).mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()){
                    checkedList.add(thisEmotion);
                } else {
                    checkedList.remove(thisEmotion);
                }
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

    public ArrayList<Emotion> getCheckedList(){
        return checkedList;
    }
}
