package com.example.ex3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ex3.api.TokenAPI;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.CompletableFuture;

public class Login extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;

    private MutableLiveData<String> tokenLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize tokenLiveData
        tokenLiveData = new MutableLiveData<>();


        // Retrieve the saved theme preference
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isDarkThemeEnabled = sharedPreferences.getBoolean("DarkTheme", false);

        // Apply the saved theme preference
        if (isDarkThemeEnabled) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        // Receiving message
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(Login.this, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Move to register screen
        Button toRegister = findViewById(R.id.toRegister);
        toRegister.setOnClickListener(view -> {
            Intent i = new Intent(this, Register.class);
            startActivity(i);
        });

        // Login button
        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> {
            usernameEditText = findViewById(R.id.unameEditText);
            passwordEditText = findViewById(R.id.pswEditText);
            validateFields();
        });





    }


    private void validateFields() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            showToast("Please enter a username");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showToast("Please enter a password");
            return;
        }

        // All fields are valid
        handleLogin();
    }

    private void performLogin(String username, String password) {
        Intent i = new Intent(Login.this, Home.class);
        startActivity(i);
        tokenLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String token) {
                if (!TextUtils.isEmpty(token)) {
                    // Token has been retrieved, proceed with further actions
                    handleToken(token, username);
                } else {
                    showToast("Incorrect username or password");
                }
            }
        });

        //mainActivityViewModel.getToken(username, password, tokenLiveData);
    }

    private void handleToken(String token, String username) {
        // Save the token in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Token", token);
        editor.putString("User", username);
        editor.apply();

        Intent i = new Intent(Login.this, Home.class);
        startActivity(i);
    }


    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0); // Adjust the gravity here
        toast.show();
    }

    private void handleLogin() {
        // Get username and password from the EditText fields
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        TokenAPI tokenAPI = TokenAPI.getInstance();
        CompletableFuture<String> tokenFuture = tokenAPI.createToken(username, password);

        tokenFuture.thenAccept(token -> {
            String bearerToken = "Bearer \"" + token + "\"";
            navigateToContacts(bearerToken);
        }).exceptionally(error -> {
            String errorMessage = error.getCause().getMessage();
            showToast(errorMessage);
            return null;
        });
    }

    private void navigateToContacts(String token) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Start the Chat activity
                Intent intent = new Intent(Login.this, Home.class);
                intent.putExtra("token", token); // Pass the token as an extra
                startActivity(intent);
            }
        });
    }


    private void navigateToRegister() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Start the Register activity
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish(); // Finish the current activity so that the user cannot navigate back to it
            }
        });
    }


}
