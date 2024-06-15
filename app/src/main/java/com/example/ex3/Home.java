package com.example.ex3;

import static com.example.ex3.MyApplication.context;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ex3.adapters.CategoryAdapter;
import com.example.ex3.adapters.ChosenStoresAdapter;
import com.example.ex3.adapters.TagsAdapter;
import com.example.ex3.api.CategoryAPI;
import com.example.ex3.api.FavoritesAPI;
import com.example.ex3.api.TagAPI;
import com.example.ex3.daos.CategoryDao;
import com.example.ex3.entities.Category;
import com.example.ex3.entities.Store;
import com.example.ex3.localDB.AppDB;
import com.example.ex3.utils.UserPreferencesUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Home extends AppCompatActivity implements ChosenStoresAdapter.OnRemoveClickListener {
    private RecyclerView categoriesList;
    private static final int REQUEST_CODE_FAVORITES = 1;

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
        fetchFavorites();
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
        fetchFavorites();
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
                    startActivity(intent);
                    return true;
                case R.id.menu_favorites:
                    intent = new Intent(Home.this, Favorites.class);
                    startActivityForResult(intent, REQUEST_CODE_FAVORITES);
                    return true;
                default:
                    return false;
            }
        });

        tagsAdapter.setOnTagClickListener(tag -> fetchCategoryForTag(tag));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FAVORITES && resultCode == RESULT_OK) {
            chosenStores = UserPreferencesUtils.getChosenStores(context);
            chosenStoresAdapter.updateChosenStores(chosenStores);
            updateBadge();
            categoryAdapter.updateChosenStores(chosenStores);
        }
    }

    private void fetchInitialData() {
        fetchTypes(bearerToken);
        fetchDataFromServer();
    }

    private void fetchDataFromServer() {
        showLoadingIndicator(true);
        categories.clear();
        if (Objects.equals(currentSearchQuery, "")) {
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
        CategoryAPI categoryAPI = CategoryAPI.getInstance();

        CompletableFuture<List<Store>> future = categoryAPI.getStoresByName(token, query);

        future.thenAccept(storeList -> {
            Set<String> uniqueStoreTypes = new HashSet<>();
            for (Store store : storeList) {
                uniqueStoreTypes.add(store.getStoreType());
            }

            List<Category> c = new ArrayList<>();
            for (String storeType : uniqueStoreTypes) {
                List<Store> storesOfType = storeList.stream()
                        .filter(store -> storeType.equals(store.getStoreType()))
                        .collect(Collectors.toList());
                Category category = new Category(storeType, storesOfType);
                c.add(category);
            }
            categories.clear();
            categories.addAll(c);
            runOnUiThread(() -> categoryAdapter.notifyDataSetChanged());
            showLoadingIndicator(false);
        }).exceptionally(ex -> {
            showLoadingIndicator(false);
            return null;
        });
    }

    private void fetchCategoryForTag(String tag) {
        categories.clear();
        getCategoryInBackground(tag);
    }

    private void fetchCategoryFromServer(String tag) {
        CategoryAPI categoryAPI = CategoryAPI.getInstance();
        CompletableFuture<Category> future = categoryAPI.getStoresByType(bearerToken, tag);
        future.thenAccept(this::addCategoryInBackground).exceptionally(ex -> {
            showSnackbar("Error fetching category for tag");
            return null;
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
                    fetchCategoryFromServer(tag);
                } else {
                    fetchCategoryFromServer(tag);
                }
            });
        });
    }

    private void addCategoryInBackground(Category category) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            categoryDao.deleteAll();
            categoryDao.insert(category);
            Log.d("DAO", "Inserted category: " + category.getCategoryName() + category.getStoresList());
            runOnUiThread(() -> {
                categories.clear();
                categories.add(category);
                categoryAdapter.notifyDataSetChanged();
                showLoadingIndicator(false);
            });
        });
    }

    private void fetchTypes(String token) {
        TagAPI tagAPI = TagAPI.getInstance();
        CompletableFuture<List<String>> future = tagAPI.getTypes(token);
        future.thenAccept(list -> {
            tags.addAll(list);
            tagsAdapter.notifyDataSetChanged();
        }).exceptionally(ex -> {
            showSnackbar("Error fetching types");
            return null;
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

    @SuppressLint("NotifyDataSetChanged")
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRemoveClick(int position) {
        chosenStores.remove(position);
        chosenStoresAdapter.notifyItemRemoved(position);
        chosenStoresAdapter.notifyItemRangeChanged(position, chosenStores.size());
        UserPreferencesUtils.setChosenStores(context, chosenStores);
        updateBadge();
        categoryAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        chosenStores = UserPreferencesUtils.getChosenStores(context);
        chosenStoresAdapter.updateChosenStores(chosenStores);
        updateBadge();
        categoryAdapter.updateChosenStores(chosenStores);
        bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
    }

    private void fetchFavorites() {
        String token = UserPreferencesUtils.getToken(context);
        FavoritesAPI.getInstance().getFavorites(token).thenAccept(favoriteStores -> {
            UserPreferencesUtils.setFavoriteStores(context, favoriteStores);
            runOnUiThread(() -> {
                this.favoriteStores.clear();
                this.favoriteStores.addAll(favoriteStores);
                UserPreferencesUtils.setFavoriteStores(context, favoriteStores);
                categoryAdapter.updateFavoriteStores(favoriteStores);
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> Toast.makeText(Home.this, "Failed to fetch favorites", Toast.LENGTH_SHORT).show());
            return null;
        });
    }
}
