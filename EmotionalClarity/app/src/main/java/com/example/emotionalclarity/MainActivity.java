package com.example.emotionalclarity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_MAINACTIVITY = "MainActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 202;
    private static final int REQUEST_CAMERA_USE = 201;
    private static final int MIC_REQUEST_CODE = 200;
    private static final String TAG_FILENAME = "filename";
    private static String micFileName = null;
    ImageButton micStart, cameraButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        micStart = (ImageButton) findViewById(R.id.micStart);
        micStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent micIntent = new Intent(MainActivity.this, AudioRecordTest.class);
                MainActivity.this.startActivityForResult(micIntent, MIC_REQUEST_CODE);
            }
        });

        //handler for the camera button - for personal pictures
        cameraButton = (ImageButton) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //defining the required permissions for camera use
                String[] premissionsToAsk = {Manifest.permission.CAMERA};
                // asking for camera permission
                ActivityCompat.requestPermissions(MainActivity.this, premissionsToAsk, REQUEST_CAMERA_USE);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    Log.e(TAG_MAINACTIVITY, "cameraButton onClick has Failed");
                    Toast.makeText(MainActivity.this, "cameraButton onClick has Failed!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            try{
                //handler for the Audio-Input Activity (Microphone)
                if(requestCode == MIC_REQUEST_CODE && resultCode == RESULT_OK){
                    // get the microphone input from the activity and display on screen
                    if(data != null && data.hasExtra(TAG_FILENAME)){
                        micFileName = data.getExtras().getString(TAG_FILENAME);
                        ImageButton playButton = (ImageButton) findViewById(R.id.playButton);
                        playButton.setVisibility(View.VISIBLE);
                    }

                    //handler for the Camera-Activity
                } else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
                    Toast.makeText(this, "onActivityResult was activated - into the else - as in camera activity was initiated", Toast.LENGTH_LONG).show();
                    Log.i(TAG_MAINACTIVITY, "Camera Activity returned!");
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    ImageView picture = (ImageView) findViewById(R.id.pictureImageView);
                    Log.i(TAG_MAINACTIVITY,"I got to the picture setting part!!! trying to put it now!");
                    picture.setImageBitmap(imageBitmap);
                }
                //catches the NullPointerException made by hasExtra method or getString method
            }catch (NullPointerException e){
                e.printStackTrace();
                Log.e(TAG_MAINACTIVITY, "Method hasExtra or getString failed - onActivityResult");
            }
    }


    public void micOutputListener(View v){
        if(micFileName == null) return;
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(micFileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch(IOException e){
            Log.e("MainActivity", "Mic activity result failed");
            e.printStackTrace();
        }

    }

}
