package com.example.ex3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Bundle;

import com.example.ex3.adapters.TagsAdapter;
import com.example.ex3.api.TagAPI;
import com.example.ex3.entities.Store;
import com.example.ex3.fetchers.StoreFetcher;
import com.example.ex3.fetchers.TagFetcher;
import com.example.ex3.objects.Category;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.annotation.SuppressLint;import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ex3.adapters.CategoryAdapter;
import com.example.ex3.entities.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    private User me;
    String bearerToken;
    TextView badgeTextView;
    private final List<Store> chosenStores = new ArrayList<>();
    private final List<Category> categories = new ArrayList<>();
    private String currentSearchQuery = "";
    private final List<String> storeTypes = new ArrayList<>();
    private RecyclerView tagsRecyclerView;
    private TagsAdapter tagsAdapter;
    private final List<String> tags = new ArrayList<>();

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved theme preference
        SharedPreferences sharedPreferencesSettings = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isDarkThemeEnabled = sharedPreferencesSettings.getBoolean("DarkTheme", false);
        // Apply the saved theme preference
        if (isDarkThemeEnabled) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores_list);
        // Retrieve the Intent that started this activity
        Intent intent = getIntent();

        // Extract token & get user details from database
        // TODO: fetch the details of the user
        String myName = "Noy";

        // Initialize RecyclerView for tags
        tagsRecyclerView = findViewById(R.id.tags);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tagsAdapter = new TagsAdapter(tags);
        tagsRecyclerView.setAdapter(tagsAdapter);





        // Add contact button
        FloatingActionButton addContactButton = findViewById(R.id.addContact);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: navigate to the next intent
            }
        });
        // Retrieve the token from the Intent extras
        bearerToken = intent.getStringExtra("token");
        fetchTypes(bearerToken,"Azrieli TLV");
        //SwipeRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        //refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        //    @Override
        //    public void onRefresh() {
        //        // Perform the refresh action here
        //        fetchDataFromServer();
        //        // Stop the refreshing animation
        //        refreshLayout.setRefreshing(false);
        //    }
        //});

        // Initialize badgeTextView
        badgeTextView = findViewById(R.id.locationBadge);
        updateBadge(); // Update badgeTextView

        // Initialize store types
        storeTypes.add("food");
        storeTypes.add("fashion and sports");
        storeTypes.add("fashion");
        storeTypes.add("shoes");
        storeTypes.add("electricity");
        storeTypes.add("accessories & jewelries");


        tagsAdapter.setOnTagClickListener(new TagsAdapter.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                // Handle tag click
                fetchCategoryForTag(tag);
            }
        });
        // Fetch stores for each store type
        fetchDataFromServer();

        // Handle search query
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setBackgroundResource(R.drawable.bg_white_rounded);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String str) {
                if (str.isEmpty()) {
                    // Clear the search query and fetch all stores again
                    currentSearchQuery = "";
                    fetchDataFromServer();
                } else {
                    // Call server endpoint with the search query
                    currentSearchQuery = str;
                    fetchStoresByName(bearerToken, str);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // You can implement live search here if needed
                if (newText.isEmpty()) {
                    // If the search query is empty, clear the list and fetch all stores again
                    currentSearchQuery = "";
                    fetchDataFromServer();
                } else {
                    // Call server endpoint with the new search query
                    currentSearchQuery = newText;
                    fetchStoresByName(bearerToken, newText);
                }
                return true;
            }
        });
    }

    private void fetchDataFromServer() {
        categories.clear();
        if (currentSearchQuery.isEmpty()) {
            // Fetch stores by type if no search query is present
            for (String type : storeTypes) {
                fetchStoresByType(bearerToken, type);
            }
        } else {
            // Fetch stores by name if there is a search query
            fetchStoresByName(bearerToken, currentSearchQuery);
        }
    }

    private void fetchStoresByName(String token, String str) {
        // Call the server endpoint to fetch stores by name
        StoreFetcher storeFetcher = new StoreFetcher();
        storeFetcher.fetchStoresByName(token, str, new StoreFetcher.FetchSearchStoresCallback() {
            @Override
            public void onSuccess(List<Category> c) {
                // Update UI with the filtered stores
                categories.clear();
                categories.addAll(c);
                updateUI();
            }

            @Override
            public void onError(Throwable throwable) {
                // Handle error
                Toast.makeText(Home.this, "Error fetching stores by name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to update the badge text based on the number of chosen stores
    private void updateBadge() {
        if (badgeTextView != null) {
            // Update the badge text with the number of chosen stores
            int numberOfChosenStores = chosenStores.size();
            if (numberOfChosenStores > 0) {
                badgeTextView.setVisibility(View.VISIBLE);
                badgeTextView.setText(String.valueOf(numberOfChosenStores));
            } else {
                badgeTextView.setVisibility(View.GONE);
            }
        }
    }
    private void fetchCategoryForTag(String tag) {
        // Call the server endpoint to fetch category based on tag
        StoreFetcher storeFetcher = new StoreFetcher();
        storeFetcher.fetchStores(bearerToken, tag, new StoreFetcher.FetchStoresCallback() {
            @Override
            public void onSuccess(Category category) {
                // Clear existing categories and add the fetched category
                categories.clear();
                categories.add(category);
                updateUI();
            }

            @Override
            public void onError(Throwable throwable) {
                // Handle error
                Toast.makeText(Home.this, "Error fetching category for tag", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateUI() {
        RecyclerView categoriesList = findViewById(R.id.categories);
        categoriesList.setBackgroundResource(R.drawable.bg_dark_rounded);
        categoriesList.setLayoutManager(new LinearLayoutManager(this));
        CategoryAdapter adapter = new CategoryAdapter(this, categories, chosenStores, badgeTextView);
        categoriesList.setAdapter(adapter);
    }

    private void fetchStoresByType(String token, String storeType) {
        StoreFetcher storeFetcher = new StoreFetcher();
        storeFetcher.fetchStores(token, storeType, new StoreFetcher.FetchStoresCallback() {
            @Override
            public void onSuccess(Category c) {
                // Add fetched category to the list
                categories.add(c);
                updateUI();
            }

            @Override
            public void onError(Throwable throwable) {
                // Handle error
                Toast.makeText(Home.this, "Error fetching " + storeType + " stores", Toast.LENGTH_SHORT).show();
                updateUI();
            }
        });
    }
    private void fetchTypes(String token, String mallname) {
        System.out.println("1");
        TagFetcher tagFetcher = new TagFetcher();
        tagFetcher.fetchTags(token, new TagFetcher.FetchTagsCallback() {
            @Override
            public void onSuccess(List<String> c) {
                // show tags
                tags.addAll(c);
                tags.add("noy");
                tags.add("noy");
                tags.add("noy");
                tags.add("noy");
                tags.add("noy");
                tags.add("noy");
                tags.add("noy");
                tags.add("noy");
                tagsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable throwable) {
                // Handle error
                Toast.makeText(Home.this, "Error fetching types", Toast.LENGTH_SHORT).show();
                updateUI();
            }
        });
    }

    // TODO: revert this to intent...
    private void openSettingsDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_settings);
        dialog.setTitle("Settings");

        // Initialize views
        Switch themeSwitch = dialog.findViewById(R.id.themeSwitch);
        Button saveButton = dialog.findViewById(R.id.saveButton);

        // Load saved theme preference
        SharedPreferences sharedPreferencesSettings = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isDarkThemeEnabled = sharedPreferencesSettings.getBoolean("DarkTheme", false);
        themeSwitch.setChecked(isDarkThemeEnabled);

        // Theme switch listener
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save theme preference
                SharedPreferences.Editor editor = sharedPreferencesSettings.edit();
                editor.putBoolean("DarkTheme", isChecked);
                editor.apply();
                // Restart the activity to apply the theme changes
                Intent intent = new Intent(Home.this, Home.class);
                finish();
                startActivity(intent);
            }
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}