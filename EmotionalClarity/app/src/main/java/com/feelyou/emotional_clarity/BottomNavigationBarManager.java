package com.feelyou.emotional_clarity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BottomNavigationBarManager extends AppCompatActivity {
    LinearLayout layout;
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        navigateFromBar();
    }
    public void navigateFromBar(){
        layout = findViewById(R.id.appBarLayout);
        navigationView = layout.findViewById(R.id.navigationBar);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId){
                    case R.id.home: {
                        startActivity(new Intent(BottomNavigationBarManager.this, MainActivity.class));
                        break;
                    }
                    case R.id.history: {
                        // TBA
                        break;
                    }
                    case R.id.personal_profile: {
                        // TBA
                        break;
                    }
                    default: {
                        return false;
                    }
                }
                return true;
            }
        });
    }

    abstract int getLayoutId();
}
