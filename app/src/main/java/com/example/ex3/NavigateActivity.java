package com.example.ex3;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigateActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        // Get the token from the intent extras
        token = getIntent().getStringExtra("token");

        // Initialize the BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_nav_menu);

        // Set the "Navigate" item as checked in the bottom navigation view
        bottomNavigationView.getMenu().findItem(R.id.menu_navigate).setChecked(true);

        // Set up the item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        // Navigate to Home activity and pass the token
                        Intent homeIntent = new Intent(NavigateActivity.this, Home.class);
                        homeIntent.putExtra("token", token);
                        startActivity(homeIntent);
                        finish(); // Close current activity
                        return true;
                    case R.id.menu_navigate:
                        // Already on the Navigate screen, do nothing
                        return true;
                    case R.id.menu_settings:
                        // Navigate to Settings activity and pass the token
                        Intent settingsIntent = new Intent(NavigateActivity.this, SettingsActivity.class);
                        settingsIntent.putExtra("token", token);
                        startActivity(settingsIntent);
                        finish(); // Close current activity
                        return true;
                }
                return false;
            }
        });
    }
}
