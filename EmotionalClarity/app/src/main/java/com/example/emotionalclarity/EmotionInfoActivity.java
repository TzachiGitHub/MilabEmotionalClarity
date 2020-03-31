package com.example.emotionalclarity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class EmotionInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_info);
        Intent intent = getIntent();
        TextView name = (TextView) findViewById(R.id.nameText);
        TextView definition = (TextView) findViewById(R.id.infoText);
        name.setText(intent.getStringExtra("NAME"));
        definition.setText(intent.getStringExtra("DEFINITION"));

        Button backButton = (Button) findViewById(R.id.backToEmotionsListButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(v.getContext(), FeelingsActivity.class);
                startActivity(backIntent);
            }
        });
    }
}
