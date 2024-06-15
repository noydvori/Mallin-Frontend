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

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        intent = new Intent(NavigateActivity.this, Home.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.menu_navigate:
                        intent = new Intent(NavigateActivity.this, NavigateActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_favorites:
                        return true;
                }
                return false;
            }
        });
    }
}
