package com.example.emotionalclarity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.constraintlayout.widget.ConstraintSet;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import java.util.ArrayList;

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
    //private static final String REQUEST_URL = "http://192.168.43.154:8080/"; //To use in IDC
    private static final String REQUEST_URL = "http://192.168.1.193"; // My home

    //addFeelings - array intent button
    //nextButton - next screen button
    private Button addFeelings, nextButton;

    //feeling buttons resources

    ArrayList<Integer> tags;
    ArrayList<ButtonPositioning> buttonPositionings;
    int lastFeeling = 5;

    ArrayList<Integer> images;
    int lastImage = 0;

    int[] feelingTags;
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
/*
        //feeling buttons 
  array for the onClick method

        tags = new ArrayList<>();
        buttonPositionings = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            tags.add(0);
            buttonPositionings.add(new ButtonPositioning(316, 88));
        }
        */

        //to see if they were clicked or not
        //TODO - more setup required

        //currently arbitrary chose 6 feelings to be displayed
        numFeelings = 6;
        feelingTags = new int[numFeelings];

        //getting the list of feelings from the hardcoded values
        String[] feelingsNames = getResources().getStringArray(R.array.feelingsArray);

        Button[] chosenFeelings = new Button[feelingTags.length];
        chosenFeelings[0] = findViewById(R.id.circleFeeling);
        chosenFeelings[1] = findViewById(R.id.circleFeeling1);
        chosenFeelings[2] = findViewById(R.id.circleFeeling2);
        chosenFeelings[3] = findViewById(R.id.circleFeeling3);
        chosenFeelings[4] = findViewById(R.id.circleFeeling4);
        chosenFeelings[5] = findViewById(R.id.circleFeeling5);
        for(int i = 0; i < numFeelings; i++){
            chosenFeelings[i].setText(feelingsNames[i]);
        }



        // If returning from FeelingsActivity, add new chosen emotions
        try {
            Intent returnIntent = getIntent();
            ArrayList<Emotion> emotionsToAdd = returnIntent
                    .getParcelableArrayListExtra("EMOTIONS_TO_ADD");
            ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.mainActivityLayout);
            for (Emotion e : emotionsToAdd) {
                Log.i(TAG_MAINACTIVITY, e.toString());
                Button newEmotion = new Button(this);
                newEmotion.setLayoutParams(new ConstraintLayout.
                        LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT));
                newEmotion.setTag(++lastFeeling);
                newEmotion.setText(e.name);
                newEmotion.setTextSize(10);
                newEmotion.setAllCaps(false);
                newEmotion.setBackground(ContextCompat.
                        getDrawable(this, R.drawable.btn_background));
                newEmotion.setWidth(60);
                newEmotion.setHeight(60);
                newEmotion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    public void onClick(View v) {
                        feelingsListener(v);
                    }
                });
                layout.addView(newEmotion);
                tags.add(0);
            }
        } catch (NullPointerException e){}

        //add feelings button handler
        addFeelings = (Button) findViewById(R.id.addFeelings);
        addFeelings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addingFeelingsIntent = new Intent(MainActivity.this, FeelingsActivity.class);
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
                finalBundle.putIntArray(TAG_CHOSEN_FEELINGS, feelingTags);

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


                //Camera-return-Activity handler
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.mainActivityLayout);
                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.imagesLayout);

                Log.i(TAG_MAINACTIVITY, "Camera Activity returned!");
                Bundle extras = data.getExtras();

                imageBitmap = (Bitmap) extras.get("data");
                ImageView pic = (ImageView) new ImageView(this);
                pic.setId(lastImage);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMarginStart(10);
                lp.setMarginEnd(10);
                pic.setLayoutParams(lp);
                pic.setImageBitmap(imageBitmap);
                linearLayout.addView(pic);
                images.add(lastImage++);

                /*
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layout);
                constraintSet.connect(pic.getId(), ConstraintSet.BOTTOM,
                        R.id.userInput, ConstraintSet.TOP);
                if (lastImage == 1) {
                    constraintSet.connect(pic.getId(), ConstraintSet.START,
                            R.id.mainActivityLayout, ConstraintSet.START);
                } else {
                    constraintSet.connect(pic.getId(), ConstraintSet.START,
                            lastImage - 1, ConstraintSet.END);
                }
                constraintSet.applyTo(layout);

                 */
                //ImageView picture = (ImageView) findViewById(R.id.pictureImageView);
                //picture.setImageBitmap(imageBitmap);
            } else if (requestCode == TEXTBOX_REQUEST_CODE && resultCode == RESULT_OK){
                if (data != null && data.hasExtra(TEXTBOX_KEY)) {
                    userInput = (TextView) findViewById(R.id.userInput);
                    userInput.setText(data.getExtras().getString(TEXTBOX_KEY));
                    userInput.setMovementMethod(new ScrollingMovementMethod());
                }
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
        if(tags.get(position) == 0){
            tags.set(position, 1);
            backgroundShape.setColor(Color.BLUE);
        } else {
            tags.set(position, 0);
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
