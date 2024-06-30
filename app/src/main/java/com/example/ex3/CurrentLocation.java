package com.example.ex3;
import static com.example.ex3.MyApplication.context;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ex3.api.CategoryAPI;
import com.example.ex3.entities.Category;
import com.example.ex3.entities.Store;
import com.example.ex3.managers.CurrentLocationWifiManager;
import com.example.ex3.utils.UserPreferencesUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CurrentLocation extends AppCompatActivity {

    private Button buttonConfirm;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private List<String> stringsStoresList = new ArrayList<>();
    private String bearerToken;
    private List<Store> chosenStores;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private CurrentLocationWifiManager currentLocationWifiManager;

    private Button buttonCapture;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);
        bearerToken = UserPreferencesUtils.getToken(context);
        chosenStores = UserPreferencesUtils.getChosenStores(context);
        CategoryAPI categoryAPI = CategoryAPI.getInstance();

        CompletableFuture<Category> future = categoryAPI.getStoresByType(bearerToken, "all");

        future.thenAccept(category -> {
            for (Store store : category.getStoresList()) {
                stringsStoresList.add(store.getStorename());
            }
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
                if (chosenStores.size() == 1) {
                    Intent intent = new Intent(CurrentLocation.this, NavigateActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CurrentLocation.this, ConfirmPath.class);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(CurrentLocation.this, "Location is not correct", Toast.LENGTH_SHORT).show();
            }
        });

        updateButtonState("");
        Button buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> {

            // Navigate to Home activity
            Intent homeIntent = new Intent(CurrentLocation.this, Home.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(homeIntent);
        });

        // Initialize the BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_nav_menu);

        // Set up the item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:

                        // Navigate to Home activity
                        Intent homeIntent = new Intent(CurrentLocation.this, Home.class);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        startActivity(homeIntent);
                        return true;
                    case R.id.menu_navigate:
                        // Navigate to NavigateActivity and pass the token
                        Intent navigateIntent = new Intent(CurrentLocation.this, NavigateActivity.class);


                        startActivity(navigateIntent);
                        return true;
                    case R.id.menu_favorites:
                        // Navigate to SettingsActivity and pass the token
                        Intent favoritesIntent = new Intent(CurrentLocation.this, Favorites.class);


                        // Assuming bearerToken is your token variable
                        startActivity(favoritesIntent);
                        return true;
                }
                return false;
            }
        });
        buttonCapture = findViewById(R.id.button_capture);
        buttonCapture.setOnClickListener(v -> checkCameraPermission());
        initializeWifiManager();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission is required to take a photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            detectLogo(imageBitmap);
        }
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
        if (stringsStoresList.contains(v)) {
            return true;
        }
        return false;
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
        currentLocationWifiManager = new CurrentLocationWifiManager(this);
    }


    // Method to update the state of the confirm button
    private void updateButtonState(String v) {
        buttonConfirm.setEnabled(isLocationCorrect(v));
    }
}
