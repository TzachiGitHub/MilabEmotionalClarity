package com.example.emotionalclarity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationHost, FragmentCommunication {

    private static final String TAG_MAINACTIVITY = "MainActivity";
    private String userInputString;

    //volley and firebase related variables
    private RequestQueue _queue;
    private static String token = "";
    //private static final String REQUEST_URL = "http://192.168.43.154:3000/";
    private static final String REQUEST_URL = "https://us-central1-emotional-clarity-9ced0.cloudfunctions.net/app";

    User currentUser;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private FirebaseFunctions mFunctions;

    RelativeLayout loadingPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFunctions = FirebaseFunctions.getInstance("us-central1");
        // Send token to server
        _queue = Volley.newRequestQueue(this);
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
        });

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
                    // Hide keyboard:
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert mgr != null;
                    mgr.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                if (userInputString.length() > 0) {
                    // Show loading panel and send request to analyze text:
                    loadingPanel.setVisibility(View.VISIBLE);
                    sendAnalysisRequest(userInputString);
                    /*
                    sendAnalysisRequest(userInputString).addOnCompleteListener(new OnCompleteListener<JSONObject>() {

                        @Override
                        public void onComplete(@NonNull Task<JSONObject> task) {
                            if (!task.isSuccessful()) {
                                Exception e = task.getException();
                                if (e instanceof FirebaseFunctionsException) {
                                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                    FirebaseFunctionsException.Code code = ffe.getCode();
                                    Object details = ffe.getDetails();
                                }

                                // [START_EXCLUDE]
                                Log.w(TAG_MAINACTIVITY, "onFailure", e);
                                return;
                                // [END_EXCLUDE]
                            }

                            JSONObject result = task.getResult();
                            Intent intent = new Intent(MainActivity.this, FeelingsActivity.class);
                            intent.putExtra("result", result.toString());
                            intent.putExtra("text", userInputString);
                            // Make loading panel vanish:
                            loadingPanel.setVisibility(View.GONE);
                            startActivity(intent);
                        }
                    });
                    */
                } else {
                    // If no input was given:
                    Toast.makeText(v.getContext(), "Please share some input to proceed",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /**
     * Send HTTP request to server and handle response
     *
     * @param text - the user's input text which will be analyzed
     */
    public void sendAnalysisRequest(final String text) {
        //public void sendAnalysisRequest(final String text) {
        //public Task<JSONObject> sendAnalysisRequest(final String text) {
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
                    intent.putExtra("text", text);
                    // Make loading panel vanish:
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


        /*
        return mFunctions.getHttpsCallable("/analyze").call(requestObject)
                .continueWith(new Continuation<HttpsCallableResult, JSONObject>() {
                    @Override
                    public JSONObject then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        JSONObject result = (JSONObject) Objects.requireNonNull(task.getResult()).getData();
                        return result;
                    }
                });
        */
    }

    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainActivityLayout, fragment);
        if (addToBackstack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    /**
     * Checks whether the user exists in the database
     * If so, retrieves their info
     * If not, redirects them to sign-up fragment
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
                    navigateTo(new UserDetailsFragment(), true);
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

    /**
     * Used to communicate with the UserDetailsFragment
     * Acquires the user's name and gender from the fragment
     * @param objects - Name and gender
     */
    @Override
    public void getInformation(Object... objects){
        currentUser = new User(objects[0].toString(), objects[1].toString());
        db.child("users").child(token).setValue(currentUser);
        updateHeadline(currentUser.getName());
    }
}
