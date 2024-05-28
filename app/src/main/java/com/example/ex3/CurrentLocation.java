package com.example.ex3;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ex3.api.WebServiceAPI;
import com.example.ex3.entities.Category;
import com.example.ex3.entities.Store;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrentLocation extends AppCompatActivity {

    private Button buttonConfirm;
    private List<Store> storesList = new ArrayList<>();
    private List<String> stringsStoresList = new ArrayList<>();
    private String token;
    private WebServiceAPI webServiceAPI;
    private List<Store> chosenStores;
    private List<Store> favoriteStores;


    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        chosenStores = getIntent().getParcelableArrayListExtra("chosenStores");
        if (chosenStores == null) {
            chosenStores = new ArrayList<>();
        }
        favoriteStores = getIntent().getParcelableArrayListExtra("favoriteStores");


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.153.1:5000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);

        token = getIntent().getStringExtra("token");

        // Fetch stores asynchronously
        fetchStoresAsync(token, "all").thenAccept(category -> {
            storesList.addAll(category.getStoresList());
            for (Store store : storesList) {
                stringsStoresList.add(store.getStorename());
            }

            // Update the UI on the main thread
            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        CurrentLocation.this,
                        android.R.layout.select_dialog_item,
                        stringsStoresList);

                AutoCompleteTextView actv = findViewById(R.id.autoCompleteTextView);
                actv.setThreshold(1);
                actv.setAdapter(adapter);
                actv.setTextColor(Color.RED);

                // Set up listener to update button state when text changes
                actv.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // Not needed
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // Update button state when text changes
                        updateButtonState(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // Not needed
                    }
                });
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() ->
                    Toast.makeText(CurrentLocation.this, "Error fetching stores", Toast.LENGTH_SHORT).show());
            return null;
        });

        buttonConfirm = findViewById(R.id.button_confirm);
        buttonConfirm.setOnClickListener(v -> {
            // Get the text from the AutoCompleteTextView
            AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
            String location = autoCompleteTextView.getText().toString();

            // Check if location is correct before proceeding
            if (isLocationCorrect(location)) {
                if(chosenStores.size() == 1) {
                    Intent intent = new Intent(CurrentLocation.this, NavigateActivity.class);
                    intent.putParcelableArrayListExtra("chosenStores", new ArrayList<>(chosenStores));
                    intent.putParcelableArrayListExtra("favoriteStores", new ArrayList<>(favoriteStores));

                    intent.putExtra("token", token); // Assuming bearerToken is your token variable
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CurrentLocation.this, ConfirmPath.class);
                    intent.putParcelableArrayListExtra("chosenStores", new ArrayList<>(chosenStores));
                    intent.putParcelableArrayListExtra("favoriteStores", new ArrayList<>(favoriteStores));

                    intent.putExtra("token", token); // Assuming bearerToken is your token variable
                    startActivity(intent);
                }
            } else {
                Toast.makeText(CurrentLocation.this, "Location is not correct", Toast.LENGTH_SHORT).show();
            }
        });

        updateButtonState("");
        Button buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(CurrentLocation.this, Home.class);
            intent.putParcelableArrayListExtra("chosenStores", new ArrayList<>(chosenStores));
            intent.putParcelableArrayListExtra("favoriteStores", new ArrayList<>(favoriteStores));


            intent.putExtra("token", token);
            startActivity(intent);
        });

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
                        Intent navigateIntent = new Intent(CurrentLocation.this, NavigateActivity.class);
                        navigateIntent.putExtra("token", token); // Assuming bearerToken is your token variable
                        navigateIntent.putParcelableArrayListExtra("favoriteStores", new ArrayList<>(favoriteStores));
                        navigateIntent.putParcelableArrayListExtra("chosenStores", new ArrayList<>(chosenStores));


                        startActivity(navigateIntent);
                        return true;
                    case R.id.menu_favorites:
                        // Navigate to SettingsActivity and pass the token
                        Intent favoritesIntent = new Intent(CurrentLocation.this, Favorites.class);
                        favoritesIntent.putExtra("token", token);
                        favoritesIntent.putParcelableArrayListExtra("favoriteStores", new ArrayList<>(favoriteStores));
                        favoritesIntent.putParcelableArrayListExtra("chosenStores", new ArrayList<>(chosenStores));


                        // Assuming bearerToken is your token variable
                        startActivity(favoritesIntent);
                        return true;
                }
                return false;
            }
        });
    }

    // Method to check if the location is correct
    private boolean isLocationCorrect(String v) {
        if(stringsStoresList.contains(v)){
            return true;
        }
        return false;
    }

    // Method to update the state of the confirm button
    private void updateButtonState(String v) {
        buttonConfirm.setEnabled(isLocationCorrect(v));
    }

    public CompletableFuture<Category> fetchStoresAsync(String token, String storeType) {
        Call<Category> call = this.webServiceAPI.getStoresByType(token, storeType, "Azrieli TLV");
        CompletableFuture<Category> future = new CompletableFuture<>();

        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful()) {
                    Category responseCategory = response.body();
                    if (responseCategory != null) {
                        future.complete(responseCategory);
                    } else {
                        future.completeExceptionally(new Error("Response body is null"));
                    }
                } else {
                    future.completeExceptionally(new Error("Invalid token"));
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });

        return future;
    }
}
