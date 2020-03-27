package com.example.emotionalclarity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.util.ArrayList;

public class FeelingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feelings);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.EmotionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Emotion> emotionList = new ArrayList<Emotion>();
        emotionList.add(new Emotion("Fear", "I AM VERY SCARED"));
        emotionList.add(new Emotion("Sadness", "BOO HOO HOO"));
        emotionList.add(new  Emotion("Happiness", "Clap along if you feel like a room without a roof"));
        emotionList.add(new Emotion("Love", "I love u Tzachi"));

        //Intent intent = getIntent();
        recyclerView.setAdapter(new EmotionsAdapter(emotionList));
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
    }

}
