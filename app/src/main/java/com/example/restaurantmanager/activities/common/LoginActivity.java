package com.example.restaurantmanager.activities.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.activities.guest.GuestDashboardActivity;
import com.example.restaurantmanager.api.ApiService;
import com.example.restaurantmanager.models.User;
import com.example.restaurantmanager.activities.utils.SessionManager;

/**
 LoginActivity - User authentication
 Threading: API calls run on worker threads
 SOLID: Single Responsibility - handles login only
 */
public class LoginActivity extends AppCompatActivity {

    // UI Components
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView unsuccessfulSignupLabel;
    private ImageView backArrow;
    private ProgressBar loadingSpinner;

    // Services
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize services
        apiService = ApiService.getInstance(this);
        sessionManager = SessionManager.getInstance(this);

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            routeToCorrectDashboard(sessionManager.getUserType());
            finish();
            return;
        }

        initializeViews();
        setupListeners();
    }

    //Initialize views
    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        unsuccessfulSignupLabel = findViewById(R.id.unsuccessfulSignup);
        backArrow = findViewById(R.id.backArrow);

        // Hide error message initially
        unsuccessfulSignupLabel.setVisibility(View.INVISIBLE);


    }

    //Set up button listeners
    private void setupListeners() {
        // Back button
        backArrow.setOnClickListener(v -> finish());

        // Login button
        loginButton.setOnClickListener(v -> attemptLogin());
    }

    //Validate inputs and attempt login

    private void attemptLogin() {
        // Hide previous error messages
        unsuccessfulSignupLabel.setVisibility(View.INVISIBLE);

        // Get input values
        String username = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate inputs
        if (username.isEmpty()) {
            unsuccessfulSignupLabel.setText("Please enter your username");
            unsuccessfulSignupLabel.setVisibility(View.VISIBLE);
            emailInput.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            unsuccessfulSignupLabel.setText("Please enter your password");
            unsuccessfulSignupLabel.setVisibility(View.VISIBLE);
            passwordInput.requestFocus();
            return;
        }

        // Disable button during login
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        // Call API on worker thread
        performLogin(username, password);
    }

    //Perform login via API (runs on worker thread)

    private void performLogin(String username, String password) {
        apiService.loginUser(username, password, new ApiService.UserCallback() {
            @Override
            public void onSuccess(User user) {
                // This runs on main thread (handled by ApiService)

                // Save user session
                sessionManager.createLoginSession(user.getUsername(), user.getUsertype());

                // Show success message
                Toast.makeText(LoginActivity.this,
                        "Welcome back, " + user.getFirstname() + "!",
                        Toast.LENGTH_SHORT).show();

                // Route to correct dashboard based on user type
                routeToCorrectDashboard(user.getUsertype());

                // Close login activity
                finish();
            }

            @Override
            public void onError(String error) {
                // This runs on main thread (handled by ApiService)

                // Re-enable button
                loginButton.setEnabled(true);
                loginButton.setText(R.string.login_btn);

                // Show error message
                unsuccessfulSignupLabel.setText(error);
                unsuccessfulSignupLabel.setVisibility(View.VISIBLE);

                // Show toast
                Toast.makeText(LoginActivity.this,
                        "Login failed: " + error,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     Route user to appropriate dashboard based on user type
     Design Pattern: Factory-like routing
     */
    private void routeToCorrectDashboard(String userType) {
        Intent intent;

        if (userType.equalsIgnoreCase("staff")) {
            // TODO: Create StaffDashboardActivity in Phase 5
            Toast.makeText(this, "Staff dashboard", Toast.LENGTH_SHORT).show();
            sessionManager.logout();
            return;


        } else {
            // Guest user
            intent = new Intent(this, GuestDashboardActivity.class);
        }

        startActivity(intent);
    }
}
