package com.example.restaurantmanager.activities.common;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantmanager.R;
import com.example.restaurantmanager.activities.guest.GuestMakeReservationActivity;

public class SuccessfulSignupActivity extends AppCompatActivity {
    private Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_confirm);
        returnButton = findViewById(R.id.confirmButton);

        setupListeners();
    }

    private void setupListeners() {

        //Return btn
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        });
    }
}
