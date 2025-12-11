package com.example.restaurantmanager.activities.guest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantmanager.R;

public class GuestChangeSettingsActivity extends AppCompatActivity {

    // UI components
    private ImageView backButton;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText phoneInput;
    private EditText emailInput;

    // Eye icon toggles
    private ImageButton reservationChangesToggle;
    private ImageButton cancellationToggle;
    private ImageButton dateTimeChangeToggle;
    private ImageButton dayBeforeToggle;
    private ImageButton hourBeforeToggle;

    private Button saveChangesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_guest_change_settings);

        // Reference UI components
        backButton = findViewById(R.id.backArrow);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        phoneInput = findViewById(R.id.phoneInput);
        emailInput = findViewById(R.id.emailInput);

        reservationChangesToggle = findViewById(R.id.reservationChangesToggle);
        cancellationToggle = findViewById(R.id.cancellationToggle);
        dateTimeChangeToggle = findViewById(R.id.dateTimeChangeToggle);
        dayBeforeToggle = findViewById(R.id.dayBeforeToggle);
        hourBeforeToggle = findViewById(R.id.hourBeforeToggle);

        saveChangesButton = findViewById(R.id.saveChangesButton);

    }
}
