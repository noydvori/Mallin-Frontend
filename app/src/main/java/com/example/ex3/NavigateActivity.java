package com.example.ex3;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ex3.entities.Store;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class NavigateActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    String token;
    private List<Store> chosenStores;
    private List<Store> favoriteStores;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        // Get the token and chosenStores from the intent extras
        token = getIntent().getStringExtra("token");
        chosenStores = getIntent().getParcelableArrayListExtra("chosenStores");
        favoriteStores = getIntent().getParcelableArrayListExtra("favoriteStores");


        // Initialize the BottomNavigationView
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
                        intent.putExtra("token", token);
                        intent.putParcelableArrayListExtra("chosenStores", new ArrayList<>(chosenStores));
                        intent.putParcelableArrayListExtra("favoriteStores", new ArrayList<>(favoriteStores));

                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.menu_navigate:
                        // Current activity
                        return true;
                    case R.id.menu_favorites:
                        intent = new Intent(NavigateActivity.this, Favorites.class);
                        intent.putExtra("token", token);
                        intent.putParcelableArrayListExtra("chosenStores", new ArrayList<>(chosenStores));
                        intent.putParcelableArrayListExtra("favoriteStores", new ArrayList<>(favoriteStores));

                        startActivity(intent);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }
}
