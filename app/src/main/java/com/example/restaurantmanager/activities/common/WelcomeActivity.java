package com.example.restaurantmanager.activities.common;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantmanager.R;
import com.example.restaurantmanager.activities.guest.GuestSignupActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Button loginButton;
    private Button guestAccountButton;
    private Button staffCreateAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        loginButton = findViewById(R.id.loginButton);
        guestAccountButton = findViewById(R.id.guestAccountButton);
        staffCreateAccountButton = findViewById(R.id.staffCreateAccountButton);

        setupListeners();
    }

    private void setupListeners() {

        // Login button
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Login button
        guestAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, GuestSignupActivity.class);
            startActivity(intent);
        });
    }
}
