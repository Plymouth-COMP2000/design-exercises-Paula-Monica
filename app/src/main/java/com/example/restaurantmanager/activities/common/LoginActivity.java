package com.example.restaurantmanager.activities.common;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantmanager.R;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private TextView unsuccessfulSignupLabel;

    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.loginButton);
        backArrow = findViewById(R.id.backArrow);
        unsuccessfulSignupLabel = findViewById(R.id.unsuccessfulSignup);
    }
}
