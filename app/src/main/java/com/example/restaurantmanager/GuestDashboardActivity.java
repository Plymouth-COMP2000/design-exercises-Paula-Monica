package com.example.restaurantmanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GuestDashboardActivity extends AppCompatActivity {

    // UI components
    private Button signOutButton;
    private Button settingsButton;
    private TextView welcomeMessage;
    private Button viewMenuButton;
    private Button makeReservationButton;
    private Button myReservationsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_dashboard);

        signOutButton = findViewById(R.id.signOutButton);
        settingsButton = findViewById(R.id.settingsButton);
        welcomeMessage = findViewById(R.id.welcomeMessage);
        viewMenuButton = findViewById(R.id.viewMenuButton);
        makeReservationButton = findViewById(R.id.makeReservationButton);
        myReservationsButton = findViewById(R.id.myReservationsButton);

    }
}
