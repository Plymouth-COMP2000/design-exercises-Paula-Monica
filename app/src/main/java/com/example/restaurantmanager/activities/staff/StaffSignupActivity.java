package com.example.restaurantmanager.activities.staff;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantmanager.R;
import com.example.restaurantmanager.activities.common.SuccessfulSignupActivity;
import com.example.restaurantmanager.api.ApiService;
import com.example.restaurantmanager.models.User;

/**
 StaffSignupActivity - Handles staff user registration
 Design Patterns:
 - Singleton: ApiService for API calls
 - MVC: Activity as Controller, User as Model, API as data layer
 Threading:
 - ExecutorService for background API calls (handled by ApiService)
 - Handler for main thread UI updates (handled by ApiService)
 * SOLID Principles:
 - Single Responsibility: Only handles staff signup logic
 - Open/Closed: Can be extended without modification
 - Dependency Inversion: Depends on ApiService abstraction
 * HCI Principles Applied
- Visibility: Clear labels and error messages
 - Feedback: Warning messages shown for invalid input
 - Error Prevention: Real-time validation before submission
 - Consistency: Same validation patterns as GuestSignupActivity
 * Security:
 - Only accessible after valid staff code verification
 - Creates user with usertype="staff"
 */
public class StaffSignupActivity extends AppCompatActivity {

    private static final String TAG = "StaffSignupActivity";

    // UI components
    private EditText emailInput;
    private EditText usernameInput;
    private EditText firstnameInput;
    private EditText lastnameInput;
    private EditText phonenumberInput;
    private EditText passwordInput;
    private Button signupButton;
    private ImageView backArrow;

    // Warning TextViews for validation feedback
    private TextView emailWarning;
    private TextView usernameWarning;
    private TextView firstnameWarning;
    private TextView lastnameWarning;
    private TextView phonenumberWarning;
    private TextView passwordWarning;

    // Services (Singleton pattern)
    private ApiService apiService;

    // Validation constants
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MIN_CONTACT_LENGTH = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_signup);

        // Initialize API service (Singleton pattern)
        apiService = ApiService.getInstance(this);

        // Initialize UI components
        initializeViews();

        // Setup event listeners
        setupListeners();
    }

    /**
     Initialize all view components
     SOLID: Single method for view initialization
     */
    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        usernameInput = findViewById(R.id.usernameInput);
        firstnameInput = findViewById(R.id.firstnameInput);
        lastnameInput = findViewById(R.id.lastnameInput);
        phonenumberInput = findViewById(R.id.phonenumberInput);
        passwordInput = findViewById(R.id.passwordInput);
        signupButton = findViewById(R.id.signupButton);
        backArrow = findViewById(R.id.backArrow);

        // Warning messages
        emailWarning = findViewById(R.id.emailWarning);
        usernameWarning = findViewById(R.id.usernameWarning);
        firstnameWarning = findViewById(R.id.firstnameWarning);
        lastnameWarning = findViewById(R.id.lastnameWarning);
        phonenumberWarning = findViewById(R.id.phonenumberWarning);
        passwordWarning = findViewById(R.id.passwordWarning);

        // Hide all warnings initially
        hideAllWarnings();
    }

    /**
     Setup all click listeners
     SOLID: Separated listener setup from initialization
     */
    private void setupListeners() {
        // Signup button click listener
        signupButton.setOnClickListener(v -> {
            if (validateAllInputs()) {
                performSignup();
            }
        });

        // Back arrow click listener
        backArrow.setOnClickListener(v -> {
            finish(); // Return to auth code screen
        });
    }

    /**
     Hide all warning messages
     HCI: Clear feedback when user corrects input
     */
    private void hideAllWarnings() {
        emailWarning.setVisibility(View.INVISIBLE);
        usernameWarning.setVisibility(View.INVISIBLE);
        firstnameWarning.setVisibility(View.INVISIBLE);
        lastnameWarning.setVisibility(View.INVISIBLE);
        phonenumberWarning.setVisibility(View.INVISIBLE);
        passwordWarning.setVisibility(View.INVISIBLE);
    }

    /**
     Validate all input fields
     HCI: Error Prevention - validate before submission
     Consistency: Same validation logic as GuestSignupActivity
     @return true if all inputs are valid, false otherwise
     */
    private boolean validateAllInputs() {
        // Hide all warnings first
        hideAllWarnings();

        boolean isValid = true;

        // Get trimmed input values
        String email = emailInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String firstname = firstnameInput.getText().toString().trim();
        String lastname = lastnameInput.getText().toString().trim();
        String phonenumber = phonenumberInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        // Validate email
        if (TextUtils.isEmpty(email)) {
            emailWarning.setText("Email is required");
            emailWarning.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailWarning.setText("Please enter a valid email address");
            emailWarning.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Validate username
        if (TextUtils.isEmpty(username)) {
            usernameWarning.setText("Username is required");
            usernameWarning.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (username.length() < MIN_USERNAME_LENGTH) {
            usernameWarning.setText("Username must be at least " + MIN_USERNAME_LENGTH + " characters");
            usernameWarning.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (username.contains(" ")) {
            usernameWarning.setText("Username cannot contain spaces");
            usernameWarning.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Validate first name
        if (TextUtils.isEmpty(firstname)) {
            firstnameWarning.setText("First name is required");
            firstnameWarning.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Validate last name
        if (TextUtils.isEmpty(lastname)) {
            lastnameWarning.setText("Last name is required");
            lastnameWarning.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Validate phone number
        if (TextUtils.isEmpty(phonenumber)) {
            phonenumberWarning.setText("Phone number is required");
            phonenumberWarning.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (phonenumber.length() < MIN_CONTACT_LENGTH) {
            phonenumberWarning.setText("Phone number must be at least " + MIN_CONTACT_LENGTH + " digits");
            phonenumberWarning.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!phonenumber.matches("\\d+")) {
            phonenumberWarning.setText("Phone number must contain only digits");
            phonenumberWarning.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordWarning.setText("Password is required");
            passwordWarning.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (password.length() < MIN_PASSWORD_LENGTH) {
            passwordWarning.setText("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
            passwordWarning.setVisibility(View.VISIBLE);
            isValid = false;
        }

        return isValid;
    }

    /**
     Perform signup operation by calling API
     Threading: API call runs on worker thread (handled by ApiService)
     */
    private void performSignup() {
        // Disable signup button to prevent double submission
        // HCI: Feedback - show user that action is processing
        signupButton.setEnabled(false);
        signupButton.setText("Creating Account...");

        // Get input values
        String email = emailInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String firstname = firstnameInput.getText().toString().trim();
        String lastname = lastnameInput.getText().toString().trim();
        String phonenumber = phonenumberInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        // Create User object (Model - MVC pattern)
        // IMPORTANT: usertype is "staff" for staff accounts
        User newUser = new User(
                username,
                password,
                firstname,
                lastname,
                email,
                phonenumber,
                "staff"
        );

        Log.d(TAG, "Attempting to create staff account for username: " + username);

        // Check if username already exists first
        // This provides better user feedback (HCI principle)
        apiService.checkUserExists(username, new ApiService.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                // User already exists
                Log.w(TAG, "Username already exists: " + username);

                // Re-enable button
                signupButton.setEnabled(true);
                signupButton.setText("Sign Up");

                // Show error message
                usernameWarning.setText("Username already taken. Please choose another.");
                usernameWarning.setVisibility(View.VISIBLE);

                Toast.makeText(StaffSignupActivity.this,
                        "Username already exists",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                // User doesn't exist - proceed with creation
                // This is the success case for this check
                Log.d(TAG, "Username available, proceeding with staff account creation");

                createUserAccount(newUser);
            }
        });
    }

    /**
     Create user account via API
     Called after verifying username doesn't exist
     @param newUser User object containing account details
     */
    private void createUserAccount(User newUser) {
        // Call API to create user
        apiService.createUser(newUser, new ApiService.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                // Account created successfully
                Log.d(TAG, "Staff account created successfully: " + response);

                // HCI: Provide positive feedback
                Toast.makeText(StaffSignupActivity.this,
                        "Staff account created successfully!",
                        Toast.LENGTH_SHORT).show();

                // Navigate to success screen
                navigateToSuccessScreen();
            }

            @Override
            public void onError(String error) {
                // Account creation failed
                Log.e(TAG, "Failed to create staff account: " + error);

                // Re-enable button
                signupButton.setEnabled(true);
                signupButton.setText("Sign Up");

                // HCI: Clear error feedback
                Toast.makeText(StaffSignupActivity.this,
                        "Signup failed: " + error,
                        Toast.LENGTH_LONG).show();

                // Show specific error if possible
                if (error.toLowerCase().contains("email")) {
                    emailWarning.setText("This email is already registered");
                    emailWarning.setVisibility(View.VISIBLE);
                } else if (error.toLowerCase().contains("username")) {
                    usernameWarning.setText("Username is unavailable");
                    usernameWarning.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //Navigate to success confirmation screen
    private void navigateToSuccessScreen() {
        Intent intent = new Intent(StaffSignupActivity.this, SuccessfulSignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close signup activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cleanup: Re-enable button if activity is destroyed
        if (signupButton != null) {
            signupButton.setEnabled(true);
            signupButton.setText("Sign Up");
        }
    }
}