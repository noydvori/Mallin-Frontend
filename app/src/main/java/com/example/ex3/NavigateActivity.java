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
        // Get the token from the intent extras
        String token = getIntent().getStringExtra("token");

        // Initialize the BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_nav_menu);

        // Set up the item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        intent = new Intent(NavigateActivity.this, Home.class);
                        intent.putExtra("token", token); // Ensure token is passed
                        startActivity(intent);
                        finish(); // Close current activity
                        return true;
                    case R.id.menu_navigate:
                        // Current activity
                        return true;
                    case R.id.menu_settings:
                        intent = new Intent(NavigateActivity.this, SettingsActivity.class);
                        intent.putExtra("token", token); // Ensure token is passed
                        startActivity(intent);
                        finish(); // Close current activity
                        return true;
                }
                return false;
            }
        });
    }
}
