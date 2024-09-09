package com.example.ex3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.example.ex3.NavigateActivity;
import com.example.ex3.daos.CategoryDao;
import com.example.ex3.daos.StoreDao;
import com.example.ex3.entities.Category;
import com.example.ex3.entities.Store;
import com.example.ex3.localDB.AppDB;
import com.example.ex3.managers.StoresWifiManager;
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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Home extends AppCompatActivity{

    private static final int REQUEST_CODE_NAVIGATE = 2;
    private static final int REQUEST_CODE_FAVORITES = 1;
    private static final int MAX_PAGES = 18;

    private String bearerToken;
    private TextView badgeTextView;
    private List<Store> chosenStores;
    private final List<Category> categories = new ArrayList<>();
    private String currentSearchQuery = "";
    private final List<String> tags = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private ChosenStoresAdapter chosenStoresAdapter;
    private CategoryDao categoryDao;
    private CategoryAdapter categoryAdapter;
    private RecyclerView tagsRecyclerView;
    private BottomNavigationView bottomNavigationView;
    private TagsAdapter tagsAdapter;
    private List<Store> favoriteStores;
    private int currentPage;
    private int remainingRequests = MAX_PAGES;


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
        AppDB database = Room.databaseBuilder(getApplicationContext(), AppDB.class, "DB")
                .fallbackToDestructiveMigration()
                .build();
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            categoryDao = database.categoryDao();
            categoryDao.deleteAll();
        });
    }

    private void initializeUI() {
        UserPreferencesUtils.removeChosenStores(this);
        UserPreferencesUtils.removeClosestStores(this);
        UserPreferencesUtils.removeStoresPath(this);
        UserPreferencesUtils.removeNodesPath(this);
        bearerToken = UserPreferencesUtils.getToken(this);
        chosenStores = UserPreferencesUtils.getChosenStores(this);
        setContentView(R.layout.activity_stores_list);

        drawerLayout = findViewById(R.id.drawer_layout);
        RecyclerView chosenStoresRecyclerView = findViewById(R.id.chosenStoresRecyclerView);
        chosenStoresRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chosenStoresAdapter = new ChosenStoresAdapter(this, chosenStores);

        chosenStoresRecyclerView.setAdapter(chosenStoresAdapter);

        tagsRecyclerView = findViewById(R.id.tags);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tagsAdapter = new TagsAdapter(tags);
        tagsRecyclerView.setAdapter(tagsAdapter);
        tagsAdapter.selectTag("all");
        currentPage = 0;

        FloatingActionButton navigateButton = findViewById(R.id.navigate);
        navigateButton.setOnClickListener(v -> handleNavigateButtonClick());

        badgeTextView = findViewById(R.id.locationBadge);
        updateBadge();

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setBackgroundResource(R.drawable.bg_white_rounded);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
                // Optional: request focus to show the keyboard if it's not already visible
                searchView.requestFocus();
            }
        });

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

                // Hide the keyboard when the search text is empty
                if (newText.isEmpty()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    }
                }
                return true;
            }
        });

        RecyclerView categoriesList = findViewById(R.id.categories);
        categoriesList.setBackgroundResource(R.drawable.bg_dark_rounded);
        categoriesList.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategoryAdapter(this, categories, chosenStores, favoriteStores, badgeTextView);
        categoriesList.setAdapter(categoryAdapter);


        bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
    }
    private void fetchStoresByTypePaged(String token, String storeType, int page) {

        CategoryAPI categoryAPI = CategoryAPI.getInstance();

        CompletableFuture<List<Store>> future = categoryAPI.getStoresByTypePaged(token, storeType, page);

        future.thenAccept(storeList -> {
            runOnUiThread(() -> {
                if (storeList != null && !storeList.isEmpty()) {
                    if (categories.isEmpty()) {
                        categories.add(new Category(storeType, storeList));
                    } else {
                        categories.get(0).getStoresList().addAll(storeList);
                    }
                }
                categoryAdapter.notifyItemChanged(0);
                remainingRequests--;
                if (remainingRequests == 0 && !categories.isEmpty()) {
                    addCategoryInBackground(categories.get(0));
                }
            });
        }).exceptionally(ex -> {
            runOnUiThread(() -> {
                remainingRequests--;
                if (remainingRequests == 0) {
                    addCategoryInBackground(categories.get(0));
                }
            });
            return null;
        });
    }
    public void onChosenStoreRemoved(Store store) {
        // Update chosenStores list
        chosenStores = UserPreferencesUtils.getChosenStores(this);
        // Notify the CategoryAdapter to update the UI
        int index = categories.get(0).getStoresList().indexOf(store);
        categoryAdapter.notifyOneStore(index);
        // Optionally, update the badge or other UI elements
        updateBadge();
    }


    private void initializeListeners() {
        findViewById(R.id.locationIcon).setOnClickListener(v -> toggleDrawer());
        findViewById(R.id.favorites_icon).setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, Favorites.class);
            startActivityForResult(intent, REQUEST_CODE_FAVORITES);});

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                    Intent intent;
                    switch (item.getItemId()) {
                        case R.id.menu_home:
                            return true;
                        case R.id.menu_navigate:
                            intent = new Intent(Home.this, NavigateActivity.class);
                            startActivityForResult(intent, REQUEST_CODE_NAVIGATE);
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
            chosenStores = UserPreferencesUtils.getChosenStores(this);
            chosenStoresAdapter.updateChosenStores(chosenStores);
            updateBadge();
            categoryAdapter.updateChosenStores(chosenStores);
        }
    }


    private void fetchInitialData() {
        fetchTypes(bearerToken);
        getCategoryInBackground("all");
    }


    private void fetchDataFromServer() {
        categories.clear();
        TextView noResultsText = findViewById(R.id.no_results_text);
        noResultsText.setVisibility(View.GONE); // Hide it at the start of each search

        if (Objects.equals(currentSearchQuery, "")) {
            tagsRecyclerView.post(() -> getCategoryInBackground("all"));
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
        TextView noResultsText = findViewById(R.id.no_results_text); // Access the TextView

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

            runOnUiThread(() -> {
                categoryAdapter.notifyDataSetChanged();
                noResultsText.setVisibility(c.isEmpty() ? View.VISIBLE : View.GONE);
            });

        }).exceptionally(ex -> {

            noResultsText.setVisibility(View.VISIBLE);
            return null;
        });
    }

    private void fetchCategoryForTag(String tag) {
        categories.clear();
        getCategoryInBackground(tag);
    }
    public void getCategoryInBackground(String tag) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        //executorService.execute(() -> {
            //Category category = categoryDao.getCategory(tag);
            handler.post(() -> {
                //if (category != null && !category.getStoresList().isEmpty()) {
                //    categories.clear();
                //    categories.add(category);
                //    categoryAdapter.notifyDataSetChanged();
                //} else {
                    remainingRequests = MAX_PAGES;
                    currentPage = 0;
                    for(int i = 0; i < MAX_PAGES; i++) {
                        fetchStoresByTypePaged(bearerToken, tag, currentPage);
                        currentPage++;
                    }
                //}

            });
        //});
    }


    private void addCategoryInBackground(Category category) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            categoryDao.deleteByTagName(category.getCategoryName());
            categoryDao.insert(category);
            Log.d("DAO", "Inserted category: " + category.getCategoryName() + category.getStoresList());
            runOnUiThread(() -> {
                categories.clear();
                categories.add(category);

            });
        });
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

    private void handleNavigateButtonClick() {
        if (!UserPreferencesUtils.getChosenStores(this).isEmpty()) {
            Intent homeIntent = new Intent(Home.this, CurrentLocation.class);
            setResult(RESULT_OK, homeIntent);
            startActivity(homeIntent);
        } else {
            showSnackbar("Chosen list is empty. Add stores to continue navigate");
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

    private void fetchFavorites() {
        favoriteStores = new ArrayList<>();
        String token = UserPreferencesUtils.getToken(this);
        FavoritesAPI.getInstance().getFavorites(token).thenAccept(favoriteStores -> {
            UserPreferencesUtils.setFavoriteStores(this, favoriteStores);
            runOnUiThread(() -> {
                this.favoriteStores.clear();
                this.favoriteStores.addAll(favoriteStores);
                UserPreferencesUtils.setFavoriteStores(this, favoriteStores);
                categoryAdapter.updateFavoriteStores(favoriteStores);
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> Toast.makeText(Home.this, "Failed to fetch favorites", Toast.LENGTH_SHORT).show());
            return null;
        });
    }

    @SuppressLint("NotifyDataSetChanged")
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
    @Override
    protected void onResume() {
        super.onResume();
        chosenStores = UserPreferencesUtils.getChosenStores(this);
        chosenStoresAdapter.updateChosenStores(chosenStores);
        favoriteStores = UserPreferencesUtils.getFavoriteStores(this);
        categoryAdapter.updateChosenStores(chosenStores);
        categoryAdapter.updateFavoriteStores(favoriteStores);
        categoryAdapter.notifyDataSetChanged();
        bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
        updateBadge();
    }
}
