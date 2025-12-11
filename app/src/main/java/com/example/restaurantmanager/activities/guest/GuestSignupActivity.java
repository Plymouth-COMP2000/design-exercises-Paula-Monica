package com.example.restaurantmanager.activities.guest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantmanager.R;

public class GuestSignupActivity extends AppCompatActivity {

    // UI components
    private EditText emailInput;
    private EditText usernameInput;
    private EditText firstnameInput;
    private EditText lastnameInput;
    private EditText phonenumberInput;
    private EditText passwordInput;
    private Button signupButton;

    private TextView emailWarning;

    private TextView usernameWarning;

    private TextView firstnameWarning;

    private TextView lastnameWarning;

    private TextView phonenumberWarning;

    private TextView passwordWarning;

    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_signup);

        emailInput = findViewById(R.id.emailInput);
        usernameInput = findViewById(R.id.usernameInput);
        firstnameInput = findViewById(R.id.firstnameInput);
        lastnameInput = findViewById(R.id.lastnameInput);
        phonenumberInput = findViewById(R.id.phonenumberInput);
        passwordInput = findViewById(R.id.passwordInput);
        signupButton = findViewById(R.id.signupButton);
        backArrow = findViewById(R.id.backArrow);
        passwordWarning = findViewById(R.id.passwordWarning);
        phonenumberWarning = findViewById(R.id.phonenumberWarning);
        lastnameWarning = findViewById(R.id.lastnameWarning);
        firstnameWarning = findViewById(R.id.firstnameWarning);
        usernameWarning = findViewById(R.id.usernameWarning);
        emailWarning = findViewById(R.id.emailWarning);
    }
}
