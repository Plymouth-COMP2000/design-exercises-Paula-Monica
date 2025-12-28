package com.example.restaurantmanager.activities.staff;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantmanager.R;

public class StaffAuthCodeActivity extends AppCompatActivity {

    private static final String STAFF_AUTH_CODE = "STAFF2025";

    private EditText staffCodeInput;
    private TextView staffCodeWarning;
    private Button verifyButton;
    private TextView backToLoginLink;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_auth_code);

        staffCodeInput = findViewById(R.id.staffCodeInput);
        staffCodeWarning = findViewById(R.id.staffCodeWarning);
        verifyButton = findViewById(R.id.verifyButton);
        backToLoginLink = findViewById(R.id.backToLoginLink);
        backArrow = findViewById(R.id.backArrow);

        verifyButton.setOnClickListener(v -> verifyStaffCode());

        backToLoginLink.setOnClickListener(v -> finish());

        backArrow.setOnClickListener(v -> finish());
    }

    private void verifyStaffCode() {
        String enteredCode = staffCodeInput.getText().toString().trim();

        // Hide warning initially
        staffCodeWarning.setVisibility(View.INVISIBLE);

        if (TextUtils.isEmpty(enteredCode)) {
            staffCodeWarning.setText(R.string.staff_code_required);
            staffCodeWarning.setVisibility(View.VISIBLE);
            return;
        }

        if (enteredCode.equals(STAFF_AUTH_CODE)) {
            // Code correct > proceed to staff signup
            Intent intent = new Intent(
                    StaffAuthCodeActivity.this,
                    StaffSignupActivity.class
            );
            startActivity(intent);
        } else {
            staffCodeWarning.setText(R.string.incorrect_staff_code);
            staffCodeWarning.setVisibility(View.VISIBLE);
        }
    }
}
