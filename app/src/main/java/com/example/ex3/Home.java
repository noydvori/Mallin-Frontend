package com.example.ex3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ex3.adapters.CategoryAdapter;
import com.example.ex3.adapters.ChosenStoresAdapter;
import com.example.ex3.adapters.TagsAdapter;
import com.example.ex3.daos.CategoryDao;
import com.example.ex3.daos.StoreDao;
import com.example.ex3.entities.Category;
import com.example.ex3.entities.Store;
import com.example.ex3.fetchers.StoreFetcher;
import com.example.ex3.fetchers.TagFetcher;
import com.example.ex3.localDB.AppDB;
import com.example.ex3.entities.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Home extends AppCompatActivity implements ChosenStoresAdapter.OnRemoveClickListener {
    private RecyclerView categoriesList;
    private User me;
    private String bearerToken;
    private TextView badgeTextView;
    private List<Store> chosenStores;
    private final List<Category> categories = new ArrayList<>();
    private String currentSearchQuery = "";
    private final List<String> tags = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private RecyclerView chosenStoresRecyclerView;
    private ChosenStoresAdapter chosenStoresAdapter;
    private AppDB database;
    private CategoryDao categoryDao;
    private List<Category> categoryList;
    private CategoryAdapter categoryAdapter;
    private RecyclerView tagsRecyclerView;
    private BottomNavigationView bottomNavigationView;
    private TagsAdapter tagsAdapter;
    private List<Category> categoriesFromDB = new ArrayList<>();
    private List<Store> favoriteStores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = Room.databaseBuilder(getApplicationContext(), AppDB.class, "DB").fallbackToDestructiveMigration().build();
        categoryDao = database.categoryDao();

        bearerToken = getIntent().getStringExtra("token");
        chosenStores = getIntent().getParcelableArrayListExtra("chosenStores");
        if (chosenStores == null) {
            chosenStores = new ArrayList<>();
        }
        favoriteStores = getIntent().getParcelableArrayListExtra("favoriteStores");
        if (favoriteStores == null) {
            favoriteStores = new ArrayList<>();
        }
        setContentView(R.layout.activity_stores_list);
        drawerLayout = findViewById(R.id.drawer_layout);
        chosenStoresRecyclerView = findViewById(R.id.chosenStoresRecyclerView);
        chosenStoresRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chosenStoresAdapter = new ChosenStoresAdapter(chosenStores, this);
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
        findViewById(R.id.favorites_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent favIntent = new Intent(Home.this, Favorites.class);
                favIntent.putExtra("token", bearerToken);
                favIntent.putParcelableArrayListExtra("chosenStores", new ArrayList<>(chosenStores));
                favIntent.putParcelableArrayListExtra("favoriteStores", new ArrayList<>(favoriteStores));

                startActivity(favIntent);
            }
        });


        tagsRecyclerView = findViewById(R.id.tags);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tagsAdapter = new TagsAdapter(tags);
        tagsRecyclerView.setAdapter(tagsAdapter);
        tagsAdapter.selectTag("all");
        fetchCategoryForTag("all");

        FloatingActionButton navigateButton = findViewById(R.id.navigate);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the chosen list is empty
                if (!chosenStores.isEmpty()) {
                    Intent navigateIntent = new Intent(Home.this, CurrentLocation.class);
                    navigateIntent.putExtra("token", bearerToken);
                    navigateIntent.putParcelableArrayListExtra("chosenStores", new ArrayList<>(chosenStores));
                    navigateIntent.putParcelableArrayListExtra("favoriteStores", new ArrayList<>(favoriteStores));
                    startActivity(navigateIntent);
                } else {
                    // Show a message or handle the case where chosen list is empty
                    Snackbar.make(findViewById(android.R.id.content), "Chosen list is empty. Add stores to continue navigate", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

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
                currentSearchQuery = str.isEmpty() ? "" : str;
                fetchDataFromServer();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText.isEmpty() ? "" : newText;
                fetchDataFromServer();
                return true;
            }
        });

        categoriesList = findViewById(R.id.categories);
        categoriesList.setBackgroundResource(R.drawable.bg_dark_rounded);
        categoriesList.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategoryAdapter(this, categories, chosenStores, favoriteStores,badgeTextView);
        categoriesList.setAdapter(categoryAdapter);

        // Initialize the BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);

        // Set up the item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        // Already on the Home screen, do nothing
                        return true;
                    case R.id.menu_navigate:
                        intent = new Intent(Home.this, NavigateActivity.class);
                        intent.putExtra("token", bearerToken);
                        intent.putParcelableArrayListExtra("chosenStores", new ArrayList<>(chosenStores));
                        intent.putParcelableArrayListExtra("favoriteStores", new ArrayList<>(favoriteStores));

                        startActivity(intent);
                        return true;
                    case R.id.menu_favorites:
                        Intent favIntent = new Intent(Home.this, Favorites.class);
                        favIntent.putExtra("token", bearerToken);
                        favIntent.putParcelableArrayListExtra("chosenStores", new ArrayList<>(chosenStores));
                        favIntent.putParcelableArrayListExtra("favoriteStores", new ArrayList<>(favoriteStores));

                        startActivity(favIntent);
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
                Snackbar.make(findViewById(android.R.id.content), "Error fetching stores by name", Snackbar.LENGTH_SHORT).show();
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
        categories.clear();
        getCategoryInBackground(tag);
    }

    private void fetchCategoryFromServer(String tag) {
        StoreFetcher storeFetcher = new StoreFetcher();
        storeFetcher.fetchStores(bearerToken, tag, new StoreFetcher.FetchStoresCallback() {
            @Override
            public void onSuccess(Category category) {
                addCategoryInBackground(category);
                categories.add(category);
                updateUI();
            }

            @Override
            public void onError(Throwable throwable) {
                Snackbar.make(findViewById(android.R.id.content), "Error fetching category for tag", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void addCategoryInBackground(Category category) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                categoryDao.insert(category);
                Log.d("DAO", "Inserted category: " + category.getCategoryName()+ category.getStoresList());
            }
        });
    }

    public void getCategoryInBackground(String tag) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Category category = database.categoryDao().getCategory(tag);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (category != null && category.getStoresList().equals("[]")) {
                            Log.d("DAO", "Fetched category from DB: " + category.getCategoryName() + category.getStoresList());
                            categories.add(category);
                            updateUI();
                        } else {
                            Log.d("DAO", "No category found for tag: " + tag);
                            fetchCategoryFromServer(tag);
                        }
                    }
                });
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
            }

            @Override
            public void onError(Throwable throwable) {
                Snackbar.make(findViewById(android.R.id.content), "Error fetching types", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        categoryAdapter.notifyDataSetChanged();
    }


    @Override
    public void onRemoveClick(int position) {
        chosenStores.remove(position);
        chosenStoresAdapter.notifyItemRemoved(position);
        chosenStoresAdapter.notifyItemRangeChanged(position, chosenStores.size());
        updateBadge();
        categoryAdapter.notifyDataSetChanged();
    }
}
