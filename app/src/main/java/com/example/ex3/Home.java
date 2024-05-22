package com.example.ex3;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ex3.adapters.CategoryAdapter;
import com.example.ex3.adapters.ChosenStoresAdapter;
import com.example.ex3.adapters.TagsAdapter;
import com.example.ex3.daos.CategoryDao;
import com.example.ex3.daos.StoreDao;
import com.example.ex3.entities.User;
import com.example.ex3.fetchers.StoreFetcher;
import com.example.ex3.fetchers.TagFetcher;
import com.example.ex3.localDB.AppDB;
import com.example.ex3.entities.Category;
import com.example.ex3.entities.Store;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    private RecyclerView categoriesList;
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
    private DrawerLayout drawerLayout;
    private RecyclerView chosenStoresRecyclerView;
    private ChosenStoresAdapter chosenStoresAdapter;

    BottomNavigationView bottomNavigationView;

    private AppDB database;
    private StoreDao storeDao;
    private CategoryDao categoryDao;
    private CategoryAdapter categoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferencesSettings = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isDarkThemeEnabled = sharedPreferencesSettings.getBoolean("DarkTheme", false);
        if (isDarkThemeEnabled) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores_list);
        Intent intent = getIntent();
        String myName = "Noy";

        database = Room.databaseBuilder(getApplicationContext(),AppDB.class,"DB").build();
        categoryDao = database.categoryDao();
        drawerLayout = findViewById(R.id.drawer_layout);
        chosenStoresRecyclerView = findViewById(R.id.chosenStoresRecyclerView);
        chosenStoresRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chosenStoresAdapter = new ChosenStoresAdapter(chosenStores);
        chosenStoresRecyclerView.setAdapter(chosenStoresAdapter);

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


        tagsRecyclerView = findViewById(R.id.tags);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tagsAdapter = new TagsAdapter(tags);
        tagsRecyclerView.setAdapter(tagsAdapter);

        FloatingActionButton addContactButton = findViewById(R.id.addContact);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: navigate to the next intent
            }
        });

        bearerToken = intent.getStringExtra("token");
        fetchTypes(bearerToken);

        badgeTextView = findViewById(R.id.locationBadge);
        updateBadge();

        tagsAdapter.setOnTagClickListener(new TagsAdapter.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                fetchCategoryForTag(tag);
            }
        });

        fetchDataFromServer();

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setBackgroundResource(R.drawable.bg_white_rounded);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String str) {
                if (str.isEmpty()) {
                    currentSearchQuery = "";
                    fetchDataFromServer();
                } else {
                    currentSearchQuery = str;
                    fetchStoresByName(bearerToken, str);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    currentSearchQuery = "";
                    fetchDataFromServer();
                } else {
                    currentSearchQuery = newText;
                    fetchStoresByName(bearerToken, newText);
                }
                return true;
            }
        });

        // Ensure the "All" tag is selected if it's in the list
        tagsRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (tags.contains("all")) {
                    tagsAdapter.selectTag("all");
                    fetchCategoryForTag("all");
                }
            }
        });

        categoriesList = findViewById(R.id.categories);
        categoriesList.setBackgroundResource(R.drawable.bg_dark_rounded);
        categoriesList.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter= new CategoryAdapter(this, categories, chosenStores, badgeTextView);
        categoriesList.setAdapter(categoryAdapter);


        // Initialize the BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_nav_menu);

        // Set up the item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        // Already on the Home screen, do nothing
                        return true;
                    case R.id.menu_navigate:
                        // Navigate to NavigateActivity and pass the token
                        Intent navigateIntent = new Intent(Home.this, NavigateActivity.class);
                        navigateIntent.putExtra("token", bearerToken); // Assuming bearerToken is your token variable
                        startActivity(navigateIntent);
                        return true;
                    case R.id.menu_settings:
                        // Navigate to SettingsActivity and pass the token
                        Intent settingsIntent = new Intent(Home.this, SettingsActivity.class);
                        settingsIntent.putExtra("token", bearerToken); // Assuming bearerToken is your token variable
                        startActivity(settingsIntent);
                        return true;
                }
                return false;
            }
        });

    }

    private void fetchDataFromServer() {
        categories.clear();
        if (currentSearchQuery.isEmpty()) {
            tagsRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (tags.contains("all")) {
                        tagsAdapter.selectTag("all");
                        fetchCategoryForTag("all");
                    }
                }
            });
        } else {
            fetchStoresByName(bearerToken, currentSearchQuery);
        }
    }

    private void fetchStoresByName(String token, String str) {
        StoreFetcher storeFetcher = new StoreFetcher();
        storeFetcher.fetchStoresByName(token, str, new StoreFetcher.FetchSearchStoresCallback() {
            @Override
            public void onSuccess(List<Category> c) {
                categories.clear();
                categories.addAll(c);
                updateUI();
            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(Home.this, "Error fetching stores by name", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void fetchCategoryForTag(String tag) {
        StoreFetcher storeFetcher = new StoreFetcher();
        storeFetcher.fetchStores(bearerToken, tag, new StoreFetcher.FetchStoresCallback() {
            @Override
            public void onSuccess(Category category) {
                categories.clear();
                categories.add(category);
                updateUI();

            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(Home.this, "Error fetching category for tag", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        categoriesList = findViewById(R.id.categories);
        categoriesList.setBackgroundResource(R.drawable.bg_dark_rounded);
        categoriesList.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter= new CategoryAdapter(this, categories, chosenStores, badgeTextView);
        categoriesList.setAdapter(categoryAdapter);
    }

    private void fetchStoresByType(String token, String storeType) {
        StoreFetcher storeFetcher = new StoreFetcher();
        storeFetcher.fetchStores(token, storeType, new StoreFetcher.FetchStoresCallback() {
            @Override
            public void onSuccess(Category c) {
                categories.add(c);
                updateUI();
            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(Home.this, "Error fetching " + storeType + " stores", Toast.LENGTH_SHORT).show();
                updateUI();
            }
        });
    }

    private void fetchTypes(String token) {
        TagFetcher tagFetcher = new TagFetcher();
        tagFetcher.fetchTags(token, new TagFetcher.FetchTagsCallback() {
            @Override
            public void onSuccess(List<String> c) {
                tags.addAll(c);
                tagsAdapter.notifyDataSetChanged();
                // Ensure the "All" tag is selected if it's in the list
                if (tags.contains("all")) { // Assuming "All" is the correct case
                    tagsAdapter.selectTag("all");
                    fetchCategoryForTag("all");
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(Home.this, "Error fetching types", Toast.LENGTH_SHORT).show();
                updateUI();
            }
        });
    }

    private void openSettingsDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_settings);
        dialog.setTitle("Settings");

        Switch themeSwitch = dialog.findViewById(R.id.themeSwitch);
        Button saveButton = dialog.findViewById(R.id.saveButton);

        SharedPreferences sharedPreferencesSettings = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isDarkThemeEnabled = sharedPreferencesSettings.getBoolean("DarkTheme", false);
        themeSwitch.setChecked(isDarkThemeEnabled);

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferencesSettings.edit();
                editor.putBoolean("DarkTheme", isChecked);
                editor.apply();
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
        categoryAdapter.notifyDataSetChanged();
    }
}
