package com.example.emotionalclarity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private static final String TAG_CHOSEN_FEELINGS = "CHOSEN_FEELINGS";
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
    private String userInputString;
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
    int numFeelings;

    //drag button setup variables
    private ViewGroup rootLayout;
    private int _xDelta;
    private int _yDelta;
    Button dragBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    //   TODO - implement at the end of the app - not a must
    ////  dragButton onCreate setup
    //    rootLayout = (ViewGroup) findViewById(R.id.view_root);
    //    dragBtn = findViewById(R.id.circleFeeling5);
    ////  ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(150,150);
    ////  dragBtn.setLayoutParams(layoutParams);
    //    dragBtn.setOnTouchListener(new MoveTouchListener());



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //feeling buttons tags array for the onClick method
        //to see if they were clicked or not
        //TODO - more setup required

        //currently arbitrary chose 6 feelings to be displayed
        numFeelings = 6;
        tags = new int[numFeelings];

        //getting the list of feelings from the hardcoded values
        String[] feelingsNames = getResources().getStringArray(R.array.feelingsArray);

        Button[] chosenFeelings = new Button[tags.length];
        chosenFeelings[0] = findViewById(R.id.circleFeeling);
        chosenFeelings[1] = findViewById(R.id.circleFeeling1);
        chosenFeelings[2] = findViewById(R.id.circleFeeling2);
        chosenFeelings[3] = findViewById(R.id.circleFeeling3);
        chosenFeelings[4] = findViewById(R.id.circleFeeling4);
        chosenFeelings[5] = findViewById(R.id.circleFeeling5);
        for(int i = 0; i < numFeelings; i++){
            chosenFeelings[i].setText(feelingsNames[i]);
        }



        //add feelings button handler
        addFeelings = (Button) findViewById(R.id.addFeelings);
        addFeelings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addingFeelingsIntent = new Intent(MainActivity.this, FeelingsActivity.class);
                //MainActivity.this.startActivityForResult(addingFeelingsIntent, ADDING_FEELINGS_REQUEST_CODE);
                startActivity(addingFeelingsIntent);
            }
        });


        //text Button handler
        textButton = (ImageButton) findViewById(R.id.textButton);
        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent textboxIntent = new Intent(MainActivity.this, TextboxActivity.class);
                //checks to see if the user has previous input
                if (userInputString!= null) {
                    textboxIntent.putExtra(TEXTBOX_KEY, userInputString);
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
                Intent finalScreenIntent = new Intent(v.getContext(), finalScreen.class);
                Bundle finalBundle = new Bundle();
                Log.i(TAG_MAINACTIVITY, micFileName + "!!");
                if(micFileName != null)
                    finalBundle.putString(USER_AUDIO_KEY, micFileName);
                if(userInputString != null)
                    finalBundle.putString(USER_TEXT_INPUT_KEY, userInputString);
                if(imageBitmap != null)
                    finalBundle.putParcelable(USER_IMAGE_KEY, imageBitmap);

                //sending the array that indicates which feelings were chosen
                finalBundle.putIntArray(TAG_CHOSEN_FEELINGS, tags);

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

                    }

                    //Camera-return-Activity handler
                } else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
                    Log.i(TAG_MAINACTIVITY, "Camera Activity returned!");
                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");

                }else if(requestCode == TEXTBOX_REQUEST_CODE && resultCode == RESULT_OK){
                    if(data != null && data.hasExtra(TEXTBOX_KEY)){
                        userInputString = data.getExtras().getString(TEXTBOX_KEY);

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



    //currently only changes the feelingsButton colur
    //TODO - get the data of the buttons chosen to the final screen
    //TODO - adding them to the database of chosen feelings
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










    // THIS IS A FUTURE ADD - MOVING DRAGGED FEELING BUTTONS
    // NOT IMPLEMENTED CURRENTLY BECAUSE WE HAVE BIGGER THINGS TO WORRY ABOUT
    //TODO - add functionality to the moving button feelings - MAYBE - as an added bonus  - it's hard and irrelevant
    //drag and drop listener - for the feelings buttons
//    private final class MoveTouchListener implements View.OnTouchListener{
//        @Override
//        public boolean onTouch(View view, MotionEvent event) {
//            final int X = (int) event.getRawX();
//            final int Y = (int) event.getRawY();
//            ConstraintLayout.LayoutParams lParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
//            switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                case MotionEvent.ACTION_DOWN:
//                    _xDelta = X - lParams.leftMargin;
//                    _yDelta = Y - lParams.topMargin;
//                    break;
//                case MotionEvent.ACTION_POINTER_DOWN:
//                    break;
//                case MotionEvent.ACTION_POINTER_UP:
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    lParams.leftMargin = X - _xDelta;
////                    layoutParams.topMargin = Y - _yDelta;
////                    layoutParams.rightMargin = -250;
//                    lParams.bottomMargin = _yDelta - Y ;
//                    view.setLayoutParams(lParams);
//                    break;
//            }
//            rootLayout.invalidate();;
//            return true;
//        }
//    }


}
