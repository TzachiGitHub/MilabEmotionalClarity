package com.feelyou.emotional_clarity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UnCheckedAdapter extends RecyclerView.Adapter {
    private ArrayList<String> mDataSet;
    private ArrayList<String> mChecked = new ArrayList<>();
    public UnCheckedAdapter(ArrayList<String> emotions){
        mDataSet = emotions;
    }

    public static class CheckedViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;

        public CheckedViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.checkName);
            mCheckBox = (CheckBox) view.findViewById(R.id.checkbox);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emotion_check_item,
                parent, false);
        CheckedViewHolder holder = new CheckedViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final CheckedViewHolder myViewHolder = (CheckedViewHolder)holder;
        final String name = mDataSet.get(position);
        myViewHolder.mTextView.setText(name);
        myViewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mChecked.add(name);
                } else {
                    mChecked.remove(name);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * For getting the emotions chosen by the user.
     * @return ArrayList containing the names of chosen emotions.
     */
    ArrayList<String> getCheckedList() {
        return mChecked;
    }
}
