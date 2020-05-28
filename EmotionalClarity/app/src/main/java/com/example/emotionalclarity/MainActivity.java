package com.example.emotionalclarity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.Semaphore;

public class MainActivity extends AppCompatActivity implements NavigationHost {

    private static final String TAG_MAINACTIVITY = "MainActivity";
    /*
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
    private Bitmap imageBitmap;
    */
    private String userInputString;

    //volley and firebase related variables
    private RequestQueue _queue;
    private static String token = "";
    private static final String REQUEST_URL = "http://192.168.43.154:8080/"; //To use in IDC
    //private static final String REQUEST_URL = "http://192.168.1.193:8080/"; // My home

    /*
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
    */

    public User currentUser;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    RelativeLayout loadingPanel;

    @SuppressLint("SetTextI18n")
    public void setNewUser(String newName, String newGender) {
        currentUser = new User(newName, newGender);
        db.child("users").child(token).setValue(currentUser);
        updateHeadline(currentUser.getName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Send token to server
        _queue = Volley.newRequestQueue(this);
        AsyncOp getToken = new AsyncOp();
        getToken.execute();
        /*
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
                getUser(token);
                JSONObject requestObject = new JSONObject();
                try {
                    requestObject.put("token", token);
                }
                catch (JSONException e) {
                    Log.e(TAG_MAINACTIVITY, token);
                }
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, REQUEST_URL + "token",
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
        }); */

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        loadingPanel.setVisibility(View.GONE);
        final TextInputEditText userInput = (TextInputEditText) findViewById(R.id.userInput);
        Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInputString = Objects.requireNonNull(userInput.getText()).toString();
                // Hide keyboard:
                try {
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                if (userInputString.length() > 0) {
                    loadingPanel.setVisibility(View.VISIBLE);
                    sendAnalysisRequest(userInputString);
                } else { // If no input was given:
                    Toast.makeText(v.getContext(), "Please share some input to proceed",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //////////////////////////////////////////
    // The following is old material
    /////////////////////////////////////////

    /*

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            try{
                //Audio-Input-return-Activity handler (microphone)
                if(resultCode == RESULT_OK) {
                    if(requestCode == MIC_REQUEST_CODE) {
                        // get the microphone input from the activity and display on screen
                        if (data != null && data.hasExtra(TAG_FILENAME)) {
                            micFileName = data.getExtras().getString(TAG_FILENAME);
                        }


                    } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
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
                        Log.d(TAG_MAINACTIVITY, "here");

                    } else if (requestCode == TEXTBOX_REQUEST_CODE) {
                        if(data != null && data.hasExtra(TEXTBOX_KEY)){
                            userInputString = data.getExtras().getString(TEXTBOX_KEY);
                            TextView textView = (TextView)findViewById(R.id.userInput);
                            textView.setText(userInputString);
                        }
                    }

            }
                //catches the NullPointerException made by hasExtra method or getString method
            } catch (NullPointerException e) {
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

    */

    //////////////////////////////////////////
    // End of old material
    /////////////////////////////////////////

    /*
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
    */

    /**
     * Send HTTP request to server and handle response
     *
     * @param text - the user's input text which will be analyzed
     */
    public void sendAnalysisRequest(String text) {
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("text", text);
        } catch (JSONException e) {
            Log.d(TAG_MAINACTIVITY, "JSON error");
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, REQUEST_URL + "analyze",
                requestObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG_MAINACTIVITY, "Message sent successfully");
                try {
                    JSONObject result = response.getJSONObject("result");
                    Log.i(TAG_MAINACTIVITY, result.toString());
                    Intent intent = new Intent(MainActivity.this, FeelingsActivity.class);
                    intent.putExtra("result", result.toString());
                    intent.putExtra("text", userInputString);
                    loadingPanel.setVisibility(View.GONE);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        });
        _queue.add(req);
    }

    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment);
        if (addToBackstack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    /**
     * Navigate to sign-up fragment
     */
    public void goToSignup() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.mainActivityLayout, new UserDetailsFragment())
                .commit();
    }

    /**
     * Checks whether the user exists in the database
     * If so, retrieves their info
     * If not, redirects them to sign-up page
     *
     * @param token - device token which is the key for each user in the DB
     */
    public void getUser(String token) {
        db.child("users").child(token).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    // Navigate to fragment where new users can sign up:
                    goToSignup();
                } else {
                    currentUser = dataSnapshot.getValue(User.class);
                    assert currentUser != null;
                    updateHeadline(currentUser.getName()); // Update UI to display the user's name
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG_MAINACTIVITY, databaseError.getMessage());
            }
        });
    }

    /**
     * Updates the MainActivity headline to greet the user by name
     *
     * @param name - the user's name
     */
    @SuppressLint("SetTextI18n")
    public void updateHeadline(String name) {
        TextView headline = (TextView) findViewById(R.id.headline);
        headline.setText("Hi " + name + "! Tell me about your day.");
    }

    private class AsyncOp extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            final Semaphore semaphore = new Semaphore(0);
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    token = instanceIdResult.getToken();
                    JSONObject requestObject = new JSONObject();
                    try {
                        requestObject.put("token", token);
                        semaphore.release();
                    } catch (JSONException e) {
                        Log.e(TAG_MAINACTIVITY, token);
                        semaphore.release();
                    }
                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, REQUEST_URL + "token",
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

            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            getUser(token);
        }
    }
}
