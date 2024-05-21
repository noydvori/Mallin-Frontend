package com.example.ex3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private Switch themeSwitch;
    private String token;
    private SharedPreferences sharedPreferencesSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Get the token from the intent extras
        sharedPreferencesSettings = getSharedPreferences("Settings", MODE_PRIVATE);
        token = getIntent().getStringExtra("token");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        // Initialize the BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_nav_menu);

        // Set up the item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        // Navigate to Home activity
                        Intent homeIntent = new Intent(SettingsActivity.this, Home.class);
                        homeIntent.putExtra("token", token);
                        startActivity(homeIntent);
                        finish(); // Close current activity
                        return true;
                    case R.id.menu_navigate:
                        // Navigate to NavigateActivity
                        // Navigate to Home activity
                        Intent navIntent = new Intent(SettingsActivity.this, NavigateActivity.class);
                        navIntent.putExtra("token", token);
                        startActivity(navIntent);
                        finish(); // Close current activity
                        return true;
                    case R.id.menu_settings:
                        // Current activity
                        return true;
                }
                return false;
            }
        });

        // Mark the Settings menu item as checked
        bottomNavigationView.getMenu().findItem(R.id.menu_settings).setChecked(true);
    }
}
