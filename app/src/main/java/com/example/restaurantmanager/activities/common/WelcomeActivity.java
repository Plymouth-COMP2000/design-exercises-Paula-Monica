package com.example.restaurantmanager.activities.common;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.restaurantmanager.R;
import com.example.restaurantmanager.activities.guest.GuestSignupActivity;
import com.example.restaurantmanager.activities.staff.StaffAuthCodeActivity;
import com.example.restaurantmanager.activities.staff.StaffSignupActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Button loginButton;
    private Button guestAccountButton;
    private Button staffCreateAccountButton;


    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                // Request permission
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        loginButton = findViewById(R.id.loginButton);
        guestAccountButton = findViewById(R.id.guestAccountButton);
        staffCreateAccountButton = findViewById(R.id.staffCreateAccountButton);

        setupListeners();
        // Request notification permission
        requestNotificationPermission();
    }

    private void setupListeners() {

        // Login button
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Guest make account button
        guestAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, GuestSignupActivity.class);
            startActivity(intent);
        });

        // Staff make account button
        staffCreateAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, StaffAuthCodeActivity.class);
            startActivity(intent);
        });


    }
}
