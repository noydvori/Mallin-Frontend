package com.example.ex3;

import static com.example.ex3.MyApplication.context;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ex3.entities.Store;
import com.example.ex3.utils.UserPreferencesUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class NavigateActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    String bearerToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);
        bearerToken = UserPreferencesUtils.getToken(context);
        bottomNavigationView = findViewById(R.id.bottom_nav_menu);

        // Set the "Navigate" item as checked in the bottom navigation view
        bottomNavigationView.getMenu().findItem(R.id.menu_navigate).setChecked(true);

        // Set up the item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        intent = new Intent(NavigateActivity.this, Home.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_navigate:
                        // Current activity
                        return true;
                    case R.id.menu_favorites:
                        intent = new Intent(NavigateActivity.this, Favorites.class);startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }
}
