package com.example.ex3;

import static com.example.ex3.MyApplication.context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.ex3.adapters.CategoryAdapter;
import com.example.ex3.adapters.ChosenStoresAdapter;
import com.example.ex3.adapters.TagsAdapter;
import com.example.ex3.daos.CategoryDao;
import com.example.ex3.entities.Category;
import com.example.ex3.entities.Store;
import com.example.ex3.fetchers.StoreFetcher;
import com.example.ex3.fetchers.TagFetcher;
import com.example.ex3.localDB.AppDB;
import com.example.ex3.utils.UserPreferencesUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Home extends AppCompatActivity implements ChosenStoresAdapter.OnRemoveClickListener {
    private RecyclerView categoriesList;
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
    private CategoryAdapter categoryAdapter;
    private RecyclerView tagsRecyclerView;
    private BottomNavigationView bottomNavigationView;
    private TagsAdapter tagsAdapter;
    private List<Store> favoriteStores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeDatabase();
        initializeUI();
        initializeListeners();
        fetchInitialData();
    }

    private void initializeDatabase() {
        database = Room.databaseBuilder(getApplicationContext(), AppDB.class, "DB")
                .fallbackToDestructiveMigration()
                .build();
        categoryDao = database.categoryDao();
    }

    private void initializeUI() {
        bearerToken = UserPreferencesUtils.getToken(context);
        chosenStores = UserPreferencesUtils.getChosenStores(context);
        favoriteStores = UserPreferencesUtils.getFavoriteStores(context);
        setContentView(R.layout.activity_stores_list);

        drawerLayout = findViewById(R.id.drawer_layout);
        chosenStoresRecyclerView = findViewById(R.id.chosenStoresRecyclerView);
        chosenStoresRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chosenStoresAdapter = new ChosenStoresAdapter(chosenStores, this);
        chosenStoresRecyclerView.setAdapter(chosenStoresAdapter);

        tagsRecyclerView = findViewById(R.id.tags);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tagsAdapter = new TagsAdapter(tags);
        tagsRecyclerView.setAdapter(tagsAdapter);
        tagsAdapter.selectTag("all");

        FloatingActionButton navigateButton = findViewById(R.id.navigate);
        navigateButton.setOnClickListener(v -> handleNavigateButtonClick());

        badgeTextView = findViewById(R.id.locationBadge);
        updateBadge();

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setBackgroundResource(R.drawable.bg_white_rounded);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query.isEmpty() ? "" : query;
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
        categoryAdapter = new CategoryAdapter(this, categories, chosenStores, favoriteStores, badgeTextView);
        categoriesList.setAdapter(categoryAdapter);

        bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
        fetchCategoryForTag("all");
    }

    private void initializeListeners() {
        findViewById(R.id.locationIcon).setOnClickListener(v -> toggleDrawer());
        findViewById(R.id.favorites_icon).setOnClickListener(v -> navigateToFavorites());

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.menu_home:
                    return true;
                case R.id.menu_navigate:
                    intent = new Intent(Home.this, NavigateActivity.class);
                    break;
                case R.id.menu_favorites:
                    intent = new Intent(Home.this, Favorites.class);
                    break;
                default:
                    return false;
            }
            startActivity(intent);
            return true;
        });

        tagsAdapter.setOnTagClickListener(tag -> fetchCategoryForTag(tag));
    }

    private void fetchInitialData() {
        fetchTypes(bearerToken);
        fetchDataFromServer();
    }

    private void fetchDataFromServer() {
        showLoadingIndicator(true);
        categories.clear();
        if (currentSearchQuery.isEmpty()) {
            tagsRecyclerView.post(() -> {
                getCategoryInBackground("all");
            });
        } else {
            fetchStoresByName(bearerToken, currentSearchQuery);
        }
    }
    private void showLoadingIndicator(boolean show) {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    private void fetchStoresByName(String token, String query) {
        StoreFetcher storeFetcher = new StoreFetcher();
        storeFetcher.fetchStoresByName(token, query, new StoreFetcher.FetchSearchStoresCallback() {
            @Override
            public void onSuccess(List<Category> fetchedCategories) {
                categories.clear();
                categories.addAll(fetchedCategories);
                categoryAdapter.notifyDataSetChanged();
                showLoadingIndicator(false);
            }

            @Override
            public void onError(Throwable throwable) {
                showLoadingIndicator(false);
            }
        });
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
            }

            @Override
            public void onError(Throwable throwable) {
                showSnackbar("Error fetching category for tag");
            }
        });
    }

    private void getCategoryInBackground(String tag) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            Category category = categoryDao.getCategory(tag);
            handler.post(() -> {
                if (category != null && !category.getStoresList().isEmpty()) {
                    categories.add(category);
                    showLoadingIndicator(false);
                    categoryAdapter.notifyDataSetChanged();
                    fetchCategoryFromServer(tag); // הבאת נתונים מהשרת ועדכון UI לאחר מכן
                } else {
                    fetchCategoryFromServer(tag);
                }
            });
        });
    }

    private void addCategoryInBackground(Category category) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            categoryDao.deleteAll(); // מחיקת כל הקטגוריות הישנות
            categoryDao.insert(category); // הוספת הקטגוריה החדשה
            Log.d("DAO", "Inserted category: " + category.getCategoryName() + category.getStoresList());
            runOnUiThread(() -> {
                categories.clear();
                categories.add(category);
                categoryAdapter.notifyDataSetChanged();
                showLoadingIndicator(false); // הסתרת תצוגת הטעינה
            });
        });
    }

    private void fetchTypes(String token) {
        TagFetcher tagFetcher = new TagFetcher();
        tagFetcher.fetchTags(token, new TagFetcher.FetchTagsCallback() {
            @Override
            public void onSuccess(List<String> fetchedTags) {
                tags.addAll(fetchedTags);
                tagsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable throwable) {
                showSnackbar("Error fetching types");
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

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    private void toggleDrawer() {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.closeDrawer(Gravity.END);
        } else {
            chosenStoresAdapter.notifyDataSetChanged();
            drawerLayout.openDrawer(Gravity.END);
        }
    }

    private void navigateToFavorites() {
        Intent favIntent = new Intent(Home.this, Favorites.class);
        startActivity(favIntent);
    }

    private void handleNavigateButtonClick() {
        if (!chosenStores.isEmpty()) {
            Intent navigateIntent = new Intent(Home.this, CurrentLocation.class);
           startActivity(navigateIntent);
        } else {
            showSnackbar("Chosen list is empty. Add stores to continue navigate");
        }
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
