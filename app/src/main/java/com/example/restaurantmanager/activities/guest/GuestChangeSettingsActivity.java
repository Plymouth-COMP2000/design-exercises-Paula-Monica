package com.example.restaurantmanager.activities.guest;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.activities.utils.SessionManager;
import com.example.restaurantmanager.api.ApiService;
import com.example.restaurantmanager.models.NotificationPreferences;
import com.example.restaurantmanager.models.User;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

/**
 GuestChangeSettingsActivity - Manage guest user settings
 Design Pattern: MVC (Controller)
 SOLID: Single Responsibility - handles settings UI and updates
 Threading: API calls run on worker threads via ApiService
 */
public class GuestChangeSettingsActivity extends AppCompatActivity {

    // UI components
    private ImageView backButton;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText phoneInput;
    private EditText emailInput;

    // Notification toggles
    private ImageButton cancellationToggle;
    private ImageButton dayBeforeToggle;
    private ImageButton hourBeforeToggle;

    private Button saveChangesButton;

    // Services
    private SessionManager sessionManager;
    private ApiService apiService;
    private NotificationPreferences notificationPreferences;

    // State
    private boolean cancellationEnabled = true;
    private boolean dayBeforeEnabled = true;
    private boolean hourBeforeEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_guest_change_settings);

        //Initialise services
        sessionManager = SessionManager.getInstance(this);
        apiService = ApiService.getInstance(this);
        notificationPreferences = NotificationPreferences.getInstance(this);

        initializeViews();
        loadUserData();
        loadNotificationPreferences();
        setupListeners();
    }

    //Initialize view components
    private void initializeViews() {
        backButton = findViewById(R.id.backArrow);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        phoneInput = findViewById(R.id.phoneInput);
        emailInput = findViewById(R.id.emailInput);

        cancellationToggle = findViewById(R.id.cancellationToggle);
        dayBeforeToggle = findViewById(R.id.dayBeforeToggle);
        hourBeforeToggle = findViewById(R.id.hourBeforeToggle);

        saveChangesButton = findViewById(R.id.saveChangesButton);
    }

    /**
     Load user data from API
     Design Pattern: Callback pattern
     */
    private void loadUserData() {
        String username = sessionManager.getUsername();

        if (username == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Use read_user endpoint
        String url = "http://10.240.72.69/comp2000/coursework/read_user/10921081/" + username;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONObject userJson = response.getJSONObject("user");

                        // Populate fields
                        firstNameInput.setText(userJson.getString("firstname"));
                        lastNameInput.setText(userJson.getString("lastname"));
                        phoneInput.setText(userJson.getString("contact"));
                        emailInput.setText(userJson.getString("email"));

                        Log.d("SettingsActivity", "User loaded: " + userJson.getString("firstname"));
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing user data", Toast.LENGTH_SHORT).show();
                        Log.e("SettingsActivity", "JSON error: " + e.getMessage());
                    }
                },
                error -> {
                    Toast.makeText(this, "Could not load user data", Toast.LENGTH_SHORT).show();
                    Log.e("SettingsActivity", "Error: " + error.getMessage());
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    //Load notification preferences from SharedPreferences
    private void loadNotificationPreferences() {
        cancellationEnabled = notificationPreferences.isGuestCancellationEnabled();
        dayBeforeEnabled = notificationPreferences.isGuestDayBeforeEnabled();
        hourBeforeEnabled = notificationPreferences.isGuestHourBeforeEnabled();

        // Update toggle button states
        updateToggleState(cancellationToggle, cancellationEnabled);
        updateToggleState(dayBeforeToggle, dayBeforeEnabled);
        updateToggleState(hourBeforeToggle, hourBeforeEnabled);
    }

    //Setup click listeners
    private void setupListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Notification toggles
        cancellationToggle.setOnClickListener(v -> {
            cancellationEnabled = !cancellationEnabled;
            updateToggleState(cancellationToggle, cancellationEnabled);
        });

        dayBeforeToggle.setOnClickListener(v -> {
            dayBeforeEnabled = !dayBeforeEnabled;
            updateToggleState(dayBeforeToggle, dayBeforeEnabled);
        });

        hourBeforeToggle.setOnClickListener(v -> {
            hourBeforeEnabled = !hourBeforeEnabled;
            updateToggleState(hourBeforeToggle, hourBeforeEnabled);
        });

        // Save changes button
        saveChangesButton.setOnClickListener(v -> saveChanges());
    }

    /**
     Update toggle button visual state
     @param toggle The ImageButton to update
     @param enabled Whether notification is enabled
     */
    private void updateToggleState(ImageButton toggle, boolean enabled) {
        toggle.setSelected(enabled);
    }

    /**
     Validate and save all changes
     SOLID: Single method handles validation and save coordination
     */
    private void saveChanges() {
        // Get input values
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();

        // Validate inputs
        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save notification preferences
        notificationPreferences.setGuestCancellationEnabled(cancellationEnabled);
        notificationPreferences.setGuestDayBeforeEnabled(dayBeforeEnabled);
        notificationPreferences.setGuestHourBeforeEnabled(hourBeforeEnabled);

        // Get current username and password (password remains unchanged)
        String username = sessionManager.getUsername();

        // Create updated user object (password fetched during loadUserData)
        // For now, we'll need to fetch the current password first
        apiService.loginUser(username, "", new ApiService.UserCallback() {
            @Override
            public void onSuccess(User currentUser) {
                // Create updated user with new details but same password
                User updatedUser = new User(
                        username,
                        currentUser.getPassword(), // Keep existing password
                        firstName,
                        lastName,
                        email,
                        phone,
                        "guest"
                );

                // Update user via API (runs on worker thread)
                apiService.updateUser(username, updatedUser, new ApiService.ApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(GuestChangeSettingsActivity.this,
                                "Settings saved successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(GuestChangeSettingsActivity.this,
                                "Failed to save settings: " + error, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Toast.makeText(GuestChangeSettingsActivity.this,
                        "Could not retrieve current user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
