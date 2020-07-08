package com.feelyou.emotional_clarity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FeelingsActivity extends AppCompatActivity implements NavigationHost, FragmentCommunication {
    RecyclerView recyclerView;
    FloatingActionButton addButton;
    Button doneButton;
    static ArrayList<EmotionResponse> emotionResponses; // Emotions in the current list
    static ArrayList<String> unChosen; // The emotions that exist but aren't in the current list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feelings);
        unChosen = new EmotionMap().getKeys();
        emotionResponses = new ArrayList<>();
        Intent intent = getIntent();
        JSONObject result;
        TextView userText = (TextView) findViewById(R.id.userText);
        userText.setText(intent.getStringExtra("text"));
        userText.setMovementMethod(new ScrollingMovementMethod());
        recyclerView = (RecyclerView)findViewById(R.id.EmotionList);
        RelativeLayout bottomPart = (RelativeLayout) findViewById(R.id.bottomPart);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().heightPixels / 3);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        //layoutParams.addRule(RelativeLayout.ABOVE, R.id.appBarLayout);
        bottomPart.setLayoutParams(layoutParams);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            // Parse the response from strings to JSON:
            result = new JSONObject(Objects.requireNonNull(
                    getIntent().getStringExtra("result")));

            // Parse the response from JSON to lists:
            emotionResponses = transferFromJsonToArray(result);

            // Divide recyclerview to cells
            recyclerView.setAdapter(new EmotionsAdapter(emotionResponses,
                    new EmotionsAdapter.OnListChangeListener() {
                @Override
                public void onListChange(String name) {
                    unChosen.add(name);
                }
            }));

        } catch (JSONException e) {
            //e.printStackTrace();
        }

        addButton = (FloatingActionButton) findViewById(R.id.addButton);
        DrawableCompat.setTint(addButton.getDrawable(), ContextCompat.getColor(this, R.color.white));
        doneButton = (Button) findViewById(R.id.doneButton);
        // Add more emotions - open fragment:
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Deactivate buttons - avoid clicking when in fragment:
                addButton.setClickable(false);
                doneButton.setClickable(false);
                RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.feelingsActivityLayout);
                // Set the unChosen list as an argument to the new fragment:
                AddEmotionsFragment addEmotionsFragment = AddEmotionsFragment.newInstance(unChosen);
                // Go to fragment:
                navigateTo(addEmotionsFragment, true);
            }
        });

        // Return to first screen:
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeelingsActivity.this, MainActivity.class));
            }
        });

    }

    /*
    @Override
    int getLayoutId() {
        return R.layout.activity_feelings;
    }
    */

    private static ArrayList<EmotionResponse> transferFromJsonToArray(JSONObject object)
            throws JSONException {
        ArrayList<EmotionResponse> emotionResponseArrayList = new ArrayList<>();
        Iterator<String> keys;
        keys = object.keys();
        while (keys.hasNext()) {
            String name = keys.next();
            double score = object.getDouble(name);
            if (score > 0.2){
                unChosen.remove(name);
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
                        .replace(R.id.feelingsActivityLayout, fragment);
        if (addToBackstack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    /**
     * Used to communicate with the AddEmotionsFragment
     * Acquires the user's added emotions from the fragment
     * @param objects - ArrayList of the emotion names
     */
    @Override
    public void getInformation(Object... objects) {
        // Reactivate buttons:
        addButton.setClickable(true);
        doneButton.setClickable(true);
        // No args - return:
        if (objects == null || objects[0] == null) {
            return;
        }
        // Else - parse response:
        ArrayList<String> names = (ArrayList<String>) objects[0];
        if (names.size() == 0) {
            return;
        }
        int i = 0;
        for (String name : names) {
            unChosen.remove(name);
            emotionResponses.add(i, new EmotionResponse(name, 0.5));
            Objects.requireNonNull(recyclerView.getAdapter())
                    .notifyItemInserted(i);
            i++;
        }
        // Scroll to top of list:
        (Objects.requireNonNull(recyclerView.getLayoutManager())).scrollToPosition(0);
    }

}
