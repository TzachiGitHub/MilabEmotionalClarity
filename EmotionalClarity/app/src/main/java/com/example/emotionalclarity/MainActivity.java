package com.example.emotionalclarity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    public String TAG = "MainActivity";
    public Boolean teenagerIsActive = false;
    ImageView teenagerImageView;

    public void showPicture(View view){
        Log.i(TAG, "got into cameraOn");
        ImageView teenagerImageView = findViewById(R.id.teenager);
        if (!teenagerIsActive) {
            teenagerIsActive = true;
            teenagerImageView.animate().alpha(1).setDuration(1000);
        }
    }

    public void hidePicture(View view){
        ImageView teenagerImageView = findViewById(R.id.teenager);

        if(teenagerIsActive){
            teenagerIsActive = false;
            teenagerImageView.animate().alpha(0).setDuration(1000);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

}
