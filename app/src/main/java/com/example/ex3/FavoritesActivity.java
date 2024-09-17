package com.example.ex3;

import static com.example.ex3.MyApplication.context;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ex3.adapters.ChosenStoresAdapter;
import com.example.ex3.adapters.StoreItemAdapter;
import com.example.ex3.api.FavoritesAPI;
import com.example.ex3.entities.Store;
import com.example.ex3.utils.UserPreferencesUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity{
    private StoreItemAdapter StoreItemAdapter;

    BottomNavigationView bottomNavigationView;
    private List<Store> chosenStores;
    private List<Store> favoriteStores;
    private RecyclerView favoritesList;
    private TextView badgeTextView;
    private DrawerLayout drawerLayout;
    private RecyclerView chosenStoresRecyclerView;
    private ChosenStoresAdapter chosenStoresAdapter;
    private String bearerToken;
    private TextView noResultsText;
    private SharedPreferences sharedPreferencesSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites); // Move this line to the beginning

        bearerToken = UserPreferencesUtils.getToken(context);
        chosenStores = UserPreferencesUtils.getChosenStores(context);
        noResultsText = findViewById(R.id.no_results_text); // Now this line will not throw an exception
        favoriteStores = UserPreferencesUtils.getFavoriteStores(context);
        if(favoriteStores == null || favoriteStores.isEmpty() || favoriteStores.size() == 0){
            noResultsText.setVisibility(View.VISIBLE);
        }

        setContentView(R.layout.activity_favorites);
        bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        badgeTextView = findViewById(R.id.locationBadge);
        drawerLayout = findViewById(R.id.drawer_layout);
        chosenStoresRecyclerView = findViewById(R.id.chosenStoresRecyclerView);
        chosenStoresRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chosenStoresAdapter = new ChosenStoresAdapter(this, chosenStores);
        chosenStoresRecyclerView.setAdapter(chosenStoresAdapter);
        // Initialize searchView
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setBackgroundResource(R.drawable.bg_white_rounded);
        searchView.setBackgroundResource(R.drawable.bg_white_rounded);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
                // Optional: request focus to show the keyboard if it's not already visible
                searchView.requestFocus();
            }
        });


        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                if (newText.isEmpty()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    }
                }
                return true;
            }
        });

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


        // Set up the item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:

                        // Navigate to Home activity
                        Intent homeIntent = new Intent(FavoritesActivity.this, HomeActivity.class);
                        setResult(RESULT_OK, homeIntent);
                        startActivity(homeIntent);
                        return true;
                    case R.id.menu_navigate:
                        // Navigate to NavigateActivity
                        // Navigate to Home activity
                        Intent navIntent = new Intent(FavoritesActivity.this, NavigateActivity.class);
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
                    UserPreferencesUtils.setChosenStores(context, chosenStores);

                } else {
                    chosenStores.add(store);
                    UserPreferencesUtils.setChosenStores(context, chosenStores);
                }
                updateBadge();
            }

            @Override
            public void onStoreAddedToFavorites(Store store) {
                String bearerToken = UserPreferencesUtils.getToken(context);
                if (favoriteStores.contains(store)) {
                    favoriteStores.remove(store);
                    if (favoriteStores.isEmpty()) {
                        noResultsText.setVisibility(View.VISIBLE);
                    }
                    FavoritesAPI.getInstance().removeFromFavorites(bearerToken,store);
                    UserPreferencesUtils.removeFavoriteStore(context, store);
                } else {
                    favoriteStores.add(store);
                    FavoritesAPI.getInstance().addToFavorites(bearerToken, store);
                    UserPreferencesUtils.addFavoriteStore(context, store);

                }
                StoreItemAdapter.notifyDataSetChanged(); // Refresh the adapter to update the UI

            }

        });
        favoritesList.setAdapter(StoreItemAdapter);
        findViewById(R.id.locationIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.END)) {
                    drawerLayout.closeDrawer(Gravity.END);
                } else {
                    chosenStoresAdapter.notifyDataSetChanged();
                    drawerLayout.openDrawer(Gravity.END);
                }
            }
        });
        // Mark the Settings menu item as checked
        Button buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> onBackPressed());

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        intent = new Intent(FavoritesActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        return true;
                    case R.id.menu_navigate:
                        intent = new Intent(FavoritesActivity.this, NavigateActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_favorites:
                        return true;
                }
                return false;
            }
        });
        bottomNavigationView.getMenu().findItem(R.id.menu_favorites).setChecked(true);
        if (favoriteStores == null || favoriteStores.isEmpty()) {
            noResultsText.setVisibility(View.VISIBLE);
        }
    }
    public void onChosenStoreRemoved() {
        // Update chosenStores list
        chosenStores = UserPreferencesUtils.getChosenStores(this);
        // Notify the CategoryAdapter to update the UI
        chosenStoresAdapter.notifyDataSetChanged();
        StoreItemAdapter.notifyDataSetChanged();
        // Optionally, update the badge or other UI elements
        updateBadge();
    }

    private void filter(String query) {
        List<Store> filteredList = new ArrayList<>();
        for (Store store : favoriteStores) {
            if (store.getStoreName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(store);
            }
        }
        StoreItemAdapter.filterList(filteredList);

        if (filteredList.isEmpty()) {
            noResultsText.setVisibility(View.VISIBLE);
        } else {
            noResultsText.setVisibility(View.GONE);
        }
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
    @Override
    public void onBackPressed() {
        UserPreferencesUtils.setChosenStores(context, chosenStores); // Save the updated list
        Intent intent = new Intent(FavoritesActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        }
}
