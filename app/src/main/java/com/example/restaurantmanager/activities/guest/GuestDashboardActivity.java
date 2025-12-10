package com.example.restaurantmanager.activities.guest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantmanager.R;
import com.example.restaurantmanager.activities.common.WelcomeActivity;
import com.example.restaurantmanager.activities.utils.SessionManager;
import com.example.restaurantmanager.database.DatabaseHelper;

public class GuestDashboardActivity extends AppCompatActivity {

    // UI components
    private Button signOutButton;
    private Button settingsButton;
    private TextView welcomeMessage;
    private Button viewMenuButton;
    private Button makeReservationButton;
    private Button myReservationsButton;

    // Services
    private SessionManager sessionManager;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_dashboard);

        sessionManager = SessionManager.getInstance(this);
        databaseHelper = DatabaseHelper.getInstance(this);

        signOutButton = findViewById(R.id.signOutButton);
        settingsButton = findViewById(R.id.settingsButton);
        welcomeMessage = findViewById(R.id.welcomeMessage);
        viewMenuButton = findViewById(R.id.viewMenuButton);
        makeReservationButton = findViewById(R.id.makeReservationButton);
        myReservationsButton = findViewById(R.id.myReservationsButton);

        setupListeners();
    }

    private void showSignOutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes", (dialog, which) -> performSignOut())
                .setNegativeButton("No", null)
                .show();
    }

    private void setupListeners() {
        // Sign out button
        signOutButton.setOnClickListener(v -> showSignOutDialog());
    }

    private void performSignOut() {
        sessionManager.logout();
        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();

        // Navigate to welcome screen
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
