package com.example.ex3;

import static com.example.ex3.MyApplication.context;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import com.example.ex3.api.TokenAPI;
import com.example.ex3.utils.UserPreferencesUtils;
import com.google.android.material.snackbar.Snackbar;
import java.util.concurrent.CompletableFuture;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Move to register screen
        Button toRegister = findViewById(R.id.toRegister);
        toRegister.setOnClickListener(view -> {
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        });
        // Submit login button
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
            Snackbar.make(findViewById(android.R.id.content), "Please enter your username", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Snackbar.make(findViewById(android.R.id.content), "Please enter your password", Snackbar.LENGTH_SHORT).show();
            return;
        }
        // All fields are valid
        handleLogin();
    }

    private void handleLogin() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        TokenAPI tokenAPI = TokenAPI.getInstance();
        CompletableFuture<String> tokenFuture = tokenAPI.createToken(username, password);
        tokenFuture.thenAccept(token -> {
            String bearerToken = "Bearer \"" + token + "\"";
            navigateToContacts(bearerToken);
        }).exceptionally(error -> {
            String errorMessage = error.getCause().getMessage();
            Snackbar.make(findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_SHORT).show();
            return null;
        });
    }

    private void navigateToContacts(String token) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                UserPreferencesUtils.setToken(context, token);
                startActivity(intent);
            }
        });
    }
}
