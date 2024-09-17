package com.example.ex3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.ex3.api.UserAPI;
import com.google.android.material.snackbar.Snackbar;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText displayNameEditText;
    private Button registerButton;
    private UserAPI userAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        displayNameEditText = findViewById(R.id.displayNameEditText);
        registerButton = findViewById(R.id.registerButton);
        Button loginBtn = findViewById(R.id.toLogin);

        // Move to login screen button
        loginBtn.setOnClickListener(view -> {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        });

        // Password masking
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Submit registration
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });
    }

    // Validate input fields
    private void validateFields() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String displayName = displayNameEditText.getText().toString().trim();

        // Check if username is empty
        if (TextUtils.isEmpty(username)) {
            Snackbar.make(findViewById(android.R.id.content), "Please enter a username", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Check if password is empty
        if (TextUtils.isEmpty(password)) {
            Snackbar.make(findViewById(android.R.id.content), "Please enter a password", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Check if display name is empty
        if (TextUtils.isEmpty(displayName)) {
            Snackbar.make(findViewById(android.R.id.content), "Please enter your name", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Validate password format
        if (!isValidPassword(password)) {
            Snackbar.make(findViewById(android.R.id.content), "Password should have at least one numerical digit (0-9), length between 8 to 15 characters, one lowercase letter (a-z), and one uppercase letter (A-Z)", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // All fields are valid
        handleRegister();
    }

    // Handle registration logic
    private void handleRegister() {
        // Get values from the EditText fields
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String nickname = displayNameEditText.getText().toString().trim();

        // Get instance of UserAPI
        userAPI = UserAPI.getInstance();

        // Call API to register user
        CompletableFuture<String> future = userAPI.registerUser(username, password, nickname);

        future.thenAccept(status -> {
            if (status.equals("ok")) {
                // Navigate to login screen if registration is successful
                navigateToLogin();
            } else {
                // Show error message if registration failed
                Snackbar.make(findViewById(android.R.id.content), status, Snackbar.LENGTH_SHORT).show();
            }
        }).exceptionally(ex -> {
            // Show error message if an exception occurred
            Snackbar.make(findViewById(android.R.id.content), "An error occurred", Snackbar.LENGTH_SHORT).show();
            return null;
        });
    }

    // Validate password format
    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,16}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    // Navigate to login screen
    private void navigateToLogin() {
        Intent intent = new Intent(Register.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
