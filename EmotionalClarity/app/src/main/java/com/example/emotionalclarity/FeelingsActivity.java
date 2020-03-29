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
import android.widget.Button;

import java.util.ArrayList;

public class FeelingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feelings);
        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.EmotionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Emotion> emotionList = new ArrayList<Emotion>();
        emotionList.add(new Emotion("Afraid", "I AM VERY SCARED"));
        emotionList.add(new Emotion("Sad", "BOO HOO HOO"));
        emotionList.add(new  Emotion("Happy", "Clap along if you feel like a room without a roof"));
        emotionList.add(new Emotion("Loved", "I love u Tzachi"));

        // Divide recyclerview to cells
        recyclerView.setAdapter(new EmotionsAdapter(emotionList));
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        // Button for returning to Main Activity
        Button doneButton = (Button)findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent doneIntent = new Intent(v.getContext(), MainActivity.class);
                ArrayList<Emotion> emotionsToAdd = new ArrayList<Emotion>();
                try {
                    emotionsToAdd = ((EmotionsAdapter)recyclerView.getAdapter()).getCheckedList();
                    doneIntent.putExtra("EMOTIONS_TO_ADD", emotionsToAdd);
                } catch (NullPointerException e){}
                startActivity(doneIntent);
            }
        });
    }

}
