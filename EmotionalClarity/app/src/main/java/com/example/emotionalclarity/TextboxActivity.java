package com.example.emotionalclarity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TextboxActivity extends AppCompatActivity {

    private final String TAG_TEXTBOXACTIVITY = "TextboxActivity";
    private final String TEXTBOX_KEY = "userText";
    private EditText textbox;
    private Button finishedTextbox;
    private TextView textboxGreeting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textbox);
        Log.e(TAG_TEXTBOXACTIVITY, "hello");


        textbox = (EditText) findViewById(R.id.textbox);

        if(getIntent().getExtras() != null){
            String input = getIntent().getExtras().getString(TEXTBOX_KEY);
            textbox.setText(input);
        }

        textboxGreeting = (TextView) findViewById(R.id.textboxGreeting);

        finishedTextbox = (Button) findViewById(R.id.finishedTextbox);
        finishedTextbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent =new Intent();
                String userText = textbox.getText().toString();
                if(!userText.equals("")) {
                    returnIntent.putExtra("userText", userText);
                }
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });





    }
}
