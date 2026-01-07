package com.example.restaurantmanager.activities.guest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantmanager.R;

public class SuccessfulReservationActivity extends AppCompatActivity {
    private Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_confirmation);
        returnButton = findViewById(R.id.confirmButton);

        setupListeners();
    }

    private void setupListeners() {
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestDashboardActivity.class);
            startActivity(intent);
        });
    }
}