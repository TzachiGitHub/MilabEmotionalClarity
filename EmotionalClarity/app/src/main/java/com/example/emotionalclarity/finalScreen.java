package com.example.emotionalclarity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class finalScreen extends AppCompatActivity {

    //keys for receiving the user's data from the first screen
    private static final String USER_AUDIO_KEY = "micFileName";
    private static final String USER_TEXT_INPUT_KEY = "userInput";
    private static final String USER_IMAGE_KEY = "imageBitmap";

    TextView userInput;
    ImageButton userAudioPlayButton;
    ImageView userPicture;
    String audioFileName;
    Bitmap userPhotoBitMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_screen);

        Bundle input = getIntent().getExtras();

        if(input != null){
//            micFileName (String), userInput(TextView) (textbox),imageBitmap(Bitmap)
            audioFileName = input.getString(USER_AUDIO_KEY);


            userInput = (TextView) findViewById(R.id.userInputBox);
            userInput.setText(input.getString(USER_TEXT_INPUT_KEY));


            userPhotoBitMap = input.getParcelable(USER_IMAGE_KEY);
            if (userPhotoBitMap != null) {
                userPicture = (ImageView) findViewById(R.id.userPictureImageView);
                userPicture.setImageBitmap(userPhotoBitMap);
            }

        } else {
            Toast.makeText(this, "fuckkk", Toast.LENGTH_LONG).show();
        }

    }






    public void micOutputListener(View v){
        if(audioFileName == null) return;
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch(IOException e){
            Log.e("MainActivity", "Mic activity result failed");
            e.printStackTrace();
        }
    }
}
