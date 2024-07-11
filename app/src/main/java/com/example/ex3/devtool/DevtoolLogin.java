package com.example.ex3.devtool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ex3.R;
import com.example.ex3.adapters.SharedPreferencesAdapter;

public class DevtoolLogin extends AppCompatActivity {

    private EditText mEditText;
    private Button mLoginButotn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devtool_login);
        mEditText = findViewById(R.id.nameEditTextText);
        mLoginButotn = findViewById(R.id.devtoolLoginButton);
        mLoginButotn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mEditText.getText().toString();
                text = text.toLowerCase();
                text =text.replace(" ", "_");
                text = text + Build.MODEL;
                SharedPreferencesAdapter.getInstance(getApplicationContext()).setName(text);
                Intent intent = new Intent(DevtoolLogin.this, SplashScreen.class);
                startActivity(intent);
            }
        });


    }
}