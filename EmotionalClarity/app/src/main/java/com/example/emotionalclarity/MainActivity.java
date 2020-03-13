package com.example.emotionalclarity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_MAINACTIVITY = "MainActivity";
    private static final int ADDING_FEELINGS_REQUEST_CODE = 204;
    private static final int TEXTBOX_REQUEST_CODE = 203;
    private static final int REQUEST_IMAGE_CAPTURE = 202;
    private static final int REQUEST_CAMERA_USE = 201;
    private static final int MIC_REQUEST_CODE = 200;

    //keys for sending the user's data to the final screen
    private static final String USER_AUDIO_KEY = "micFileName";
    private static final String USER_TEXT_INPUT_KEY = "userInput";
    private static final String USER_IMAGE_KEY = "imageBitmap";

    private static final String TAG_FILENAME = "filename";
    private final String TEXTBOX_KEY = "userText";
    private static String micFileName = null;
    private ImageButton micStart, cameraButton, textButton, playButton;
    private TextView userInput;
    private Bitmap imageBitmap;

    //volley and firebase related variables
    private RequestQueue _queue;
    private static String token = "";
    private static final String username = "user";
    private static final String REQUEST_URL = "http://192.168.43.154:8080/"; //To use in IDC

    //addFeelings - array intent button
    //nextButton - next screen button
    private Button addFeelings, nextButton;

    //feeling buttons resources
    int[] tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //feeling buttons tags array for the onClick method
        tags = new int[6];


        //add feelings button handler
        addFeelings = (Button) findViewById(R.id.addFeelings);
        addFeelings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addingFeelingsIntent = new Intent(MainActivity.this, FeelingsActivity.class);
                MainActivity.this.startActivityForResult(addingFeelingsIntent, ADDING_FEELINGS_REQUEST_CODE);
            }
        });


        //text Button handler
        textButton = (ImageButton) findViewById(R.id.textButton);
        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent textboxIntent = new Intent(MainActivity.this, TextboxActivity.class);
                //checks to see if the user has previous input
                if(userInput != null && userInput.getText() != null){
                    textboxIntent.putExtra(TEXTBOX_KEY, userInput.getText().toString());
                }
                MainActivity.this.startActivityForResult(textboxIntent, TEXTBOX_REQUEST_CODE);
            }
        });


        //microphone button handler - activates the AudioRecordTest
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

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //micFileName (String), userInput(TextView) (textbox),imageBitmap(Bitmap)
                Intent finalScreenIntent = new Intent(v.getContext(), finalScreen.class);
                Bundle finalBundle = new Bundle();
                Log.i(TAG_MAINACTIVITY, micFileName + "!!");
                if(micFileName != null)
                    finalBundle.putString(USER_AUDIO_KEY, micFileName);
                if(userInput != null)
//              if(userInput != null && userInput.getText() != null)
                    finalBundle.putString(USER_TEXT_INPUT_KEY, userInput.getText().toString());
                if(imageBitmap != null)
                    finalBundle.putParcelable(USER_IMAGE_KEY, imageBitmap);
                finalScreenIntent.putExtras(finalBundle);
                MainActivity.this.startActivity(finalScreenIntent);
            }
        });

        // Send token to server
        _queue = Volley.newRequestQueue(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
                JSONObject requestObject = new JSONObject();
                try {
                    requestObject.put("token", token);
                }
                catch (JSONException e) {
                    Log.e(TAG_MAINACTIVITY, token);
                }
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, REQUEST_URL + username + "/token",
                        requestObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG_MAINACTIVITY, "Token saved successfully");
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG_MAINACTIVITY, "Failed to save token - " + error);
                            }
                        });

                _queue.add(req);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            //Audio-Input-return-Activity handler (microphone)
            if(requestCode == MIC_REQUEST_CODE && resultCode == RESULT_OK){
                // get the microphone input from the activity and display on screen
                if(data != null && data.hasExtra(TAG_FILENAME)){
                    micFileName = data.getExtras().getString(TAG_FILENAME);
                    playButton = (ImageButton) findViewById(R.id.playButton);
                    playButton.setVisibility(View.VISIBLE);
                }

                //Camera-return-Activity handler
            } else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
                Log.i(TAG_MAINACTIVITY, "Camera Activity returned!");
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                ImageView picture = (ImageView) findViewById(R.id.pictureImageView);
                picture.setImageBitmap(imageBitmap);
            }else if(requestCode == TEXTBOX_REQUEST_CODE && resultCode == RESULT_OK){
                if(data != null && data.hasExtra(TEXTBOX_KEY)){
                    userInput = (TextView) findViewById(R.id.userInput);
                    userInput.setText(data.getExtras().getString(TEXTBOX_KEY));
                    userInput.setMovementMethod(new ScrollingMovementMethod());
                }
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void feelingsListener(View v){
        //getting the backgroundShape object to change the button's colour
        GradientDrawable backgroundShape = (GradientDrawable) v.getBackground();

        int position = Integer.parseInt(v.getTag().toString());
        //position == 0 means it hasn't been clicked yet - so change the colour
        if(tags[position] == 0){
            tags[position] = 1;
            backgroundShape.setColor(Color.BLUE);
        }else{
            tags[position] = 0;
            backgroundShape.setColor(Color.WHITE);
        }



        //add to the array of chosen feelings
    }
}
