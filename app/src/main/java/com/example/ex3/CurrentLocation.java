package com.example.ex3;

import static com.example.ex3.MyApplication.context;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ex3.adapters.StoreAdapter;
import com.example.ex3.api.NavigationAPI;
import com.example.ex3.daos.CategoryDao;
import com.example.ex3.entities.Category;
import com.example.ex3.entities.Store;
import com.example.ex3.localDB.AppDB;
import com.example.ex3.managers.StoresWifiManager;
import com.example.ex3.utils.UserPreferencesUtils;
import com.example.ex3.viewModels.CurrentLocationViewModel;
import com.example.ex3.viewModels.CurrentLocationViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

public class CurrentLocation extends AppCompatActivity {

    private Button buttonConfirm;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private List<String> stringsStoresList = new ArrayList<>();
    private String bearerToken;
    private List<Store> chosenStores;
    private StoresWifiManager currentLocationWifiManager;
    private AppDB database;
    private CategoryDao categoryDao;
    private List<Store> allStores;
    private ListView suggestionsList;
    private CurrentLocationViewModel mViewModel;
    BottomNavigationView bottomNavigationView;
    StoreAdapter suggestionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_current_location);
        mViewModel = new ViewModelProvider(this, new CurrentLocationViewModelFactory()).get(CurrentLocationViewModel.class);

        bearerToken = UserPreferencesUtils.getToken(context);
        chosenStores = UserPreferencesUtils.getChosenStores(context);
        database = Room.databaseBuilder(getApplicationContext(), AppDB.class, "DB")
                .fallbackToDestructiveMigration()
                .build();
        categoryDao = database.categoryDao();


        new FetchCategoriesTask().execute();
        Log.d("Current Location", "current location");
        // Rest of your initialization code
        initializeViews();
        setupListeners();
        initializeWifiManager();
    }

    // AsyncTask to fetch categories from the database
    private class FetchCategoriesTask extends AsyncTask<Void, Void, Category> {

        @Override
        protected Category doInBackground(Void... voids) {
            return categoryDao.getCategory("all");
        }

        @Override
        protected void onPostExecute(Category category) {
            if (category != null) {
                allStores = category.getStoresList();
                for (Store store : category.getStoresList()) {
                    stringsStoresList.add(store.getStorename());
                }
                runOnUiThread(() -> {
                    AutoCompleteTextView actv = findViewById(R.id.autoCompleteTextView);
                    actv.setThreshold(1);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            CurrentLocation.this,
                            android.R.layout.select_dialog_item,
                            stringsStoresList);
                    actv.setAdapter(adapter);
                    actv.setTextColor(Color.DKGRAY);

                    actv.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // Not needed
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            updateButtonState(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            // Not needed
                        }
                    });

//                    // Display first three stores in the ListView
//                    List<Store> firstThreeStores = new ArrayList<>();
//                    // TODO: Hi Lioz, i need you to check that the closest stores get to here...
//                    List<Store> closestStores = UserPreferencesUtils.getChosenStores(context);
//                    System.out.println(closestStores);
//                    for (int i = 0; i < Math.min(3, allStores.size()); i++) {
//                        firstThreeStores.add(allStores.get(i));
//                    }


                     suggestionsAdapter = new StoreAdapter(
                            CurrentLocation.this,
                            new ArrayList<>());

                    mViewModel.getStores().observe(CurrentLocation.this, new Observer<List<Store>>() {
                        @Override
                        public void onChanged(List<Store> stores) {
                            stores.forEach(store -> {
                                Log.d("CurrentLocation: ",  "store" + store);
                            });
                            suggestionsList.post(new Runnable() {
                                @Override
                                public void run() {
                                    suggestionsAdapter.setStores(stores);
                                }
                            });


                        }
                    });

                    suggestionsList.setAdapter(suggestionsAdapter);
                    suggestionsList.setOnItemClickListener((parent, view, position, id) -> {
                        Store selectedStore = (Store) parent.getItemAtPosition(position);
                        actv.setText(selectedStore.getStorename());
                        updateButtonState(selectedStore.getStorename());
                    });
                });
            } else {
                // Handle the case where no category was found
                Toast.makeText(CurrentLocation.this, "No categories found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Initialize views and listeners
    private void initializeViews() {
        buttonConfirm = findViewById(R.id.button_confirm);
        buttonConfirm.setEnabled(false);
       suggestionsList = findViewById(R.id.suggestions_list);
    }

    private void setupListeners() {
        buttonConfirm.setOnClickListener(v -> {
            AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
            String locationName = autoCompleteTextView.getText().toString();

            if (isLocationCorrect(locationName)) {
                Store location = getStoreByName(locationName);
                UserPreferencesUtils.setLocation(location);
                if (location != null) {
                    currentLocationWifiManager.stopScan();
                    if (chosenStores.size() == 1) {
                        fetchOrderedRout(location, chosenStores);
                    } else {
                        fetchRout(location, chosenStores);
                    }
                } else {
                    Toast.makeText(CurrentLocation.this, "Store not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CurrentLocation.this, "Location is not correct", Toast.LENGTH_SHORT).show();
            }
        });

        Button buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> {
            Intent homeIntent = new Intent(CurrentLocation.this, Home.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(homeIntent);
        });


        bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_home:
                    Intent homeIntent = new Intent(CurrentLocation.this, Home.class);
                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(homeIntent);
                    return true;
                case R.id.menu_navigate:
                    Intent navigateIntent = new Intent(CurrentLocation.this, NavigateActivity.class);
                    startActivity(navigateIntent);
                    return true;
                case R.id.menu_favorites:
                    Intent favoritesIntent = new Intent(CurrentLocation.this, Favorites.class);
                    startActivity(favoritesIntent);
                    return true;
            }
            return false;
        });
    }


    private void fetchRout(Store store, List<Store> stores) {
        String token = UserPreferencesUtils.getToken(this);
        NavigationAPI.getInstance().createRout(token, store, stores).thenAccept(paths -> {
            UserPreferencesUtils.setPaths(this, paths);
            runOnUiThread(() -> {
                Intent intent = new Intent(CurrentLocation.this, ConfirmPath.class);
                startActivity(intent);
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> {
                Toast.makeText(this, "Error fetching optimal route", Toast.LENGTH_SHORT).show();
            });
            return null;
        });
    }

    private void fetchOrderedRout(Store store, List<Store> stores) {
        String token = UserPreferencesUtils.getToken(this);
        NavigationAPI.getInstance().createOrderedRout(token, store, stores).thenAccept(nodes -> {
            UserPreferencesUtils.setNodes(this, nodes);
            runOnUiThread(() -> {
                Intent intent = new Intent(CurrentLocation.this, ConfirmPath.class);
                startActivity(intent);
            });

        }).exceptionally(throwable -> {
            runOnUiThread(() -> {
                Toast.makeText(this, "Error fetching ordered route", Toast.LENGTH_SHORT).show();
            });
            return null;
        });
    }



    private void detectLogo(Bitmap bitmap) {
        // Convert bitmap to base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        // Send base64 image to server
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://your-server-url/upload";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Display the response
                    Toast.makeText(this, "Logo: " + response, Toast.LENGTH_LONG).show();
                },
                error -> {
                    // Handle error
                    Log.e("Volley", error.toString());
                    Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public byte[] getBody() {
                return encodedImage.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        queue.add(stringRequest);
    }

    // Method to check if the location is correct
    private boolean isLocationCorrect(String v) {
        return stringsStoresList.contains(v);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop WiFi scan when activity is no longer visible
        if (currentLocationWifiManager != null) {
            currentLocationWifiManager.stopScan();
        }
    }

    private void initializeWifiManager() {
        currentLocationWifiManager = new StoresWifiManager(this, mViewModel);
    }

    // Method to update the state of the confirm button
    private void updateButtonState(String v) {
        buttonConfirm.setEnabled(isLocationCorrect(v));
    }

    private Store getStoreByName(String storeName) {
        for (Store store : allStores) {
            if (store.getStorename().equals(storeName)) {
                return store;
            }
        }
        return null;
    }
}
