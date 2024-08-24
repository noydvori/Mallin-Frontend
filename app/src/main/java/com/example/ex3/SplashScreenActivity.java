package com.example.ex3;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        // Delay for the splash screen (e.g., 2 seconds) before launching the main activity
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the main activity
                Intent intent = new Intent(SplashScreenActivity.this, Login.class);
                startActivity(intent);
                // Finish the splash screen activity
                finish();
            }
        }, 2000); // 2000 milliseconds = 2 seconds
    }
}
