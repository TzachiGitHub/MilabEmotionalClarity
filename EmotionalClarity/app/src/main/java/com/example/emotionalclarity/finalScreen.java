package com.example.emotionalclarity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    private static final String TAG_CHOSEN_FEELINGS = "CHOSEN_FEELINGS";
    private static int numChosenFeelings;

    //variables to handle the user's input
    Bundle input;
    TextView userInput;
    ImageButton userAudioPlayButton;
    ImageView userPicture;
    String audioFileName;
    Bitmap userPhotoBitMap;
    int[] feelingTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_screen);

        input = getIntent().getExtras();
        if(input != null){
            //audio input handler
            audioFileName = input.getString(USER_AUDIO_KEY);
            if(audioFileName == null){
                userAudioPlayButton = (ImageButton) findViewById(R.id.userAudioPlayButton);
                userAudioPlayButton.setVisibility(View.GONE);
            }


            //textbox input handler
            userInput = (TextView) findViewById(R.id.userInputBox);
            userInput.setText(input.getString(USER_TEXT_INPUT_KEY));
            userInput.setMovementMethod(new ScrollingMovementMethod());


            //personal photo's input handler
            userPhotoBitMap = input.getParcelable(USER_IMAGE_KEY);
            if (userPhotoBitMap != null) {
                userPicture = (ImageView) findViewById(R.id.userPictureImageView);
                userPicture.setImageBitmap(userPhotoBitMap);
            }

            //chosen feelings button handler
            feelingTags = input.getIntArray(TAG_CHOSEN_FEELINGS);
            //TODO - get the num of the chosen feelings
            //currently -  arbitrary chose 6
            numChosenFeelings = 6;
            Button[] chosenFeelings = new Button[numChosenFeelings];
            chosenFeelings[0] = findViewById(R.id.circleFeelings);
            chosenFeelings[1] = findViewById(R.id.circleFeelings1);
            chosenFeelings[2] = findViewById(R.id.circleFeelings2);
            chosenFeelings[3] = findViewById(R.id.circleFeelings3);
            chosenFeelings[4] = findViewById(R.id.circleFeelings4);
            chosenFeelings[5] = findViewById(R.id.circleFeelings5);

            //getting the list of feelings from the hardcoded values
            String[] feelingsNames = getResources().getStringArray(R.array.feelingsArray);

            //runs on all the feelingTags length - to check which should stay and which should be GONE
            for(int i = 0; i < feelingTags.length; i++){
                if(feelingTags[i] == 0){
                    chosenFeelings[i].setVisibility(View.GONE);
                }else {
                    //paints the buttons that are staying - as white
                    GradientDrawable backgroundShape = (GradientDrawable) chosenFeelings[i].getBackground();
                    chosenFeelings[i].setText(feelingsNames[i]);
                    backgroundShape.setColor(Color.WHITE);
                }
            }

            //something went wrong from the first screen to the other
        } else {
            Toast.makeText(this, "the data from the first screen did not move to the final screen", Toast.LENGTH_LONG).show();
        }

    }





    //handler for the audio input from the user
    //when the audio button clicks - activates the user's recording
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
