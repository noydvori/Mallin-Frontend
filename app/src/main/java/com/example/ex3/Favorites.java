package com.example.ex3;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.getIntent;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ex3.adapters.CategoryAdapter;
import com.example.ex3.adapters.StoreItemAdapter;
import com.example.ex3.entities.FavoriteStore;
import com.example.ex3.entities.Store;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class Favorites extends AppCompatActivity {
    private StoreItemAdapter StoreItemAdapter;

    BottomNavigationView bottomNavigationView;
    private Switch themeSwitch;
    private List<Store> chosenStores;
    private List<Store> favoriteStores;
    private RecyclerView favoritesList;
    private TextView badgeTextView;




    private String token;
    private SharedPreferences sharedPreferencesSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites); // Inflate the layout first

        // Initialize badgeTextView
        badgeTextView = findViewById(R.id.locationBadge);
        // Initialize searchView
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setBackgroundResource(R.drawable.bg_white_rounded);

        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        token = getIntent().getStringExtra("token");
        favoriteStores = getIntent().getParcelableArrayListExtra("favoriteStores");
        chosenStores = getIntent().getParcelableArrayListExtra("chosenStores");

        updateBadge();

        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        updateBadge();

        // Other code...

        // Call updateBadge() after badgeTextView is initialized
        updateBadge();


        // Initialize the BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_nav_menu);

        // Set up the item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        // Navigate to Home activity
                        Intent homeIntent = new Intent(Favorites.this, Home.class);
                        homeIntent.putExtra("token", token);
                        homeIntent.putParcelableArrayListExtra("chosenStores", new ArrayList<>(chosenStores));
                        homeIntent.putParcelableArrayListExtra("favoriteStores", new ArrayList<>(favoriteStores));
                        startActivity(homeIntent);
                        return true;
                    case R.id.menu_navigate:
                        // Navigate to NavigateActivity
                        // Navigate to Home activity
                        Intent navIntent = new Intent(Favorites.this, NavigateActivity.class);
                        navIntent.putExtra("token", token);
                        navIntent.putParcelableArrayListExtra("chosenStores", new ArrayList<>(chosenStores));
                        navIntent.putParcelableArrayListExtra("favoriteStores", new ArrayList<>(favoriteStores));
                        startActivity(navIntent);
                        return true;
                    case R.id.menu_favorites:
                        return true;
                }
                return false;
            }
        });

        favoritesList = findViewById(R.id.stores);
        favoritesList.setBackgroundResource(R.drawable.bg_dark_rounded);

        StoreItemAdapter = new StoreItemAdapter(this, favoriteStores, chosenStores,favoriteStores, new StoreItemAdapter.OnStoreInteractionListener() {
            @Override
            public void onStoreAddedToList(Store store) {
                if (chosenStores.contains(store)) {
                    chosenStores.remove(store);
                } else {
                    chosenStores.add(store);
                }
                updateBadge();
                StoreItemAdapter.notifyDataSetChanged(); // Refresh the adapter to update the UI
            }

            @Override
            public void onStoreAddedToFavorites(Store store) {
                if (favoriteStores.contains(store)) {
                    favoriteStores.remove(store);
                } else {
                    favoriteStores.add(store);
                }
                StoreItemAdapter.notifyDataSetChanged(); // Refresh the adapter to update the UI
            }//         }

        });
        favoritesList.setAdapter(StoreItemAdapter);

        // Mark the Settings menu item as checked
        Button buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(Favorites.this, Home.class);
            intent.putParcelableArrayListExtra("chosenStores", new ArrayList<>(chosenStores));
            intent.putParcelableArrayListExtra("favoriteStores", new ArrayList<>(favoriteStores));

            intent.putExtra("token", token);
            startActivity(intent);
        });
        bottomNavigationView.getMenu().findItem(R.id.menu_favorites).setChecked(true);

    }
    private void filter(String query) {
        List<Store> filteredList = new ArrayList<>();
        for (Store store : favoriteStores) {
            if (store.getStoreName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(store);
            }
        }
        StoreItemAdapter.filterList(filteredList);
    }
    private void updateBadge() {
        if (badgeTextView != null) {
            int numberOfChosenStores = chosenStores.size();
            if (numberOfChosenStores > 0) {
                badgeTextView.setVisibility(View.VISIBLE);
                badgeTextView.setText(String.valueOf(numberOfChosenStores));
            } else {
                badgeTextView.setVisibility(View.GONE);
            }
        }
    }
}
