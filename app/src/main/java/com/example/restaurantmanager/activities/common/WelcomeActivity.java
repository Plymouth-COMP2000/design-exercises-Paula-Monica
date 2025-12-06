package com.example.restaurantmanager.activities.common;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantmanager.R;

public class WelcomeActivity extends AppCompatActivity {

    //UI Components
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
    }
}
