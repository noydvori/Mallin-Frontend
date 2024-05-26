package com.example.ex3;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        chosenStores = getIntent().getParcelableArrayListExtra("chosenStores");
        if (chosenStores == null) {
            chosenStores = new ArrayList<>();
        }

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
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() ->
                    Toast.makeText(CurrentLocation.this, "Error fetching stores", Toast.LENGTH_SHORT).show());
            return null;
        });

        buttonConfirm = findViewById(R.id.button_confirm);
        buttonConfirm.setOnClickListener(v -> {
            Intent intent = new Intent(CurrentLocation.this, ConfirmPath.class);
            intent.putParcelableArrayListExtra("chosenStores", new ArrayList<>(chosenStores));
            intent.putExtra("token", token); // Assuming bearerToken is your token variable
            startActivity(intent);
        });
        Button buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(CurrentLocation.this, Home.class);
            intent.putParcelableArrayListExtra("chosenStores", new ArrayList<>(chosenStores));

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
                        startActivity(navigateIntent);
                        return true;
                    case R.id.menu_settings:
                        // Navigate to SettingsActivity and pass the token
                        Intent settingsIntent = new Intent(CurrentLocation.this, SettingsActivity.class);
                        settingsIntent.putExtra("token", token);
                        // Assuming bearerToken is your token variable
                        startActivity(settingsIntent);
                        return true;
                }
                return false;
            }
        });
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
