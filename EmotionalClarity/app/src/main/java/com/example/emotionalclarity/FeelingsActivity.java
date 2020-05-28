package com.example.emotionalclarity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class FeelingsActivity extends AppCompatActivity implements NavigationHost {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feelings);
        Intent intent = getIntent();
        JSONObject result = null;
        TextView userText = (TextView) findViewById(R.id.userText);
        userText.setText(intent.getStringExtra("text"));
        userText.setMovementMethod(new ScrollingMovementMethod());

        RelativeLayout bottomPart = (RelativeLayout) findViewById(R.id.bottomPart);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().heightPixels / 3);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        bottomPart.setLayoutParams(layoutParams);

        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.EmotionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Emotion> emotionList = new ArrayList<Emotion>();

        try {
            // Parse the response from strings to JSON:
            result = new JSONObject(Objects.requireNonNull(
                    getIntent().getStringExtra("result")));

            // Parse the response from JSON to lists:
            ArrayList<EmotionResponse> emotionResponses = transferFromJsonToArray(result);

            // Divide recyclerview to cells
            recyclerView.setAdapter(new EmotionsAdapter(emotionResponses));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Add more emotions - open fragment:
        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to fragment
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.feelingsActivityLayout, new AddEmotionsFragment()).
                        addToBackStack(null)
                        .commit();
            }
        });

        // Return to first screen:
        Button doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeelingsActivity.this, MainActivity.class));
            }
        });
    }

    private static ArrayList<EmotionResponse> transferFromJsonToArray(JSONObject object)
            throws JSONException {
        ArrayList<EmotionResponse> emotionResponseArrayList = new ArrayList<>();
        Iterator<String> keys;
        keys = object.keys();
        while (keys.hasNext()) {
            String name = keys.next();
            double score = object.getDouble(name);
            if (score > 0.2){
                emotionResponseArrayList.add(new EmotionResponse(name, score));
            }
        }
        return emotionResponseArrayList;
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
}
