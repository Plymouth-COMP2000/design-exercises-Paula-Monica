package com.example.restaurantmanager.activities.staff;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.activities.utils.SessionManager;
import com.example.restaurantmanager.api.ApiService;
import com.example.restaurantmanager.models.NotificationPreferences;
import com.example.restaurantmanager.models.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 StaffSettingsActivity - Manage staff user settings
 Design Pattern: MVC (Controller)
 SOLID: Single Responsibility - handles settings UI and updates
 Threading: API calls run on worker threads via ApiService
 */
public class StaffSettingsActivity extends AppCompatActivity {

    // UI components
    private ImageView backButton;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText phoneInput;
    private EditText emailInput;

    // Notification toggles
    private ImageButton newReservationToggle;
    private ImageButton reservationChangesToggle;
    private ImageButton thirtyMinBeforeToggle;
    private ImageButton fifteenMinBeforeToggle;

    private Button saveChangesButton;

    // Services
    private SessionManager sessionManager;
    private ApiService apiService;
    private NotificationPreferences notificationPreferences;

    // State
    private boolean newReservationEnabled = true;
    private boolean reservationChangesEnabled = true;
    private boolean thirtyMinBeforeEnabled = true;
    private boolean fifteenMinBeforeEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_change_settings);

        // Initialize services
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

        newReservationToggle = findViewById(R.id.newReservationToggle);
        reservationChangesToggle = findViewById(R.id.reservationChangesToggle);
        thirtyMinBeforeToggle = findViewById(R.id.thirtyMinBeforeToggle);
        fifteenMinBeforeToggle = findViewById(R.id.fifteenMinBeforeToggle);

        saveChangesButton = findViewById(R.id.saveChangesButton);
    }

    //Load user data from API
    private void loadUserData() {
        String username = sessionManager.getUsername();

        if (username == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Direct API call to read_user endpoint
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

                        android.util.Log.d("StaffSettings", "User loaded: " + userJson.getString("firstname"));
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing user data", Toast.LENGTH_SHORT).show();
                        android.util.Log.e("StaffSettings", "JSON error: " + e.getMessage());
                    }
                },
                error -> {
                    Toast.makeText(this, "Could not load user data", Toast.LENGTH_SHORT).show();
                    android.util.Log.e("StaffSettings", "Error: " + error.getMessage());
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    //Load notification preferences from SharedPreferences
    private void loadNotificationPreferences() {
        newReservationEnabled = notificationPreferences.isStaffNewReservationEnabled();
        reservationChangesEnabled = notificationPreferences.isStaffReservationChangesEnabled();
        thirtyMinBeforeEnabled = notificationPreferences.isStaff30MinBeforeEnabled();
        fifteenMinBeforeEnabled = notificationPreferences.isStaff15MinBeforeEnabled();

        // Update toggle button states
        updateToggleState(newReservationToggle, newReservationEnabled);
        updateToggleState(reservationChangesToggle, reservationChangesEnabled);
        updateToggleState(thirtyMinBeforeToggle, thirtyMinBeforeEnabled);
        updateToggleState(fifteenMinBeforeToggle, fifteenMinBeforeEnabled);
    }

    //Setup click listeners
    private void setupListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Notification toggles
        newReservationToggle.setOnClickListener(v -> {
            newReservationEnabled = !newReservationEnabled;
            updateToggleState(newReservationToggle, newReservationEnabled);
        });

        reservationChangesToggle.setOnClickListener(v -> {
            reservationChangesEnabled = !reservationChangesEnabled;
            updateToggleState(reservationChangesToggle, reservationChangesEnabled);
        });

        thirtyMinBeforeToggle.setOnClickListener(v -> {
            thirtyMinBeforeEnabled = !thirtyMinBeforeEnabled;
            updateToggleState(thirtyMinBeforeToggle, thirtyMinBeforeEnabled);
        });

        fifteenMinBeforeToggle.setOnClickListener(v -> {
            fifteenMinBeforeEnabled = !fifteenMinBeforeEnabled;
            updateToggleState(fifteenMinBeforeToggle, fifteenMinBeforeEnabled);
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

        // Save notification preferences to SharedPreferences
        notificationPreferences.setStaffNewReservationEnabled(newReservationEnabled);
        notificationPreferences.setStaffReservationChangesEnabled(reservationChangesEnabled);
        notificationPreferences.setStaff30MinBeforeEnabled(thirtyMinBeforeEnabled);
        notificationPreferences.setStaff15MinBeforeEnabled(fifteenMinBeforeEnabled);

        String username = sessionManager.getUsername();

        // Step 1: Fetch current user to get password
        String readUrl = "http://10.240.72.69/comp2000/coursework/read_user/10921081/" + username;

        JsonObjectRequest readRequest = new JsonObjectRequest(
                Request.Method.GET,
                readUrl,
                null,
                response -> {
                    try {
                        JSONObject userJson = response.getJSONObject("user");
                        String currentPassword = userJson.getString("password");

                        // Step 2: Create updated user JSON
                        JSONObject updateJson = new JSONObject();
                        updateJson.put("username", username);
                        updateJson.put("password", currentPassword); // Keep existing password
                        updateJson.put("firstname", firstName);
                        updateJson.put("lastname", lastName);
                        updateJson.put("email", email);
                        updateJson.put("contact", phone);
                        updateJson.put("usertype", "staff");

                        // Step 3: Update user via API
                        String updateUrl = "http://10.240.72.69/comp2000/coursework/update_user/10921081/" + username;

                        JsonObjectRequest updateRequest = new JsonObjectRequest(
                                Request.Method.PUT,
                                updateUrl,
                                updateJson,
                                updateResponse -> {
                                    Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
                                    android.util.Log.d("StaffSettings", "User updated successfully");
                                    finish();
                                },
                                error -> {
                                    Toast.makeText(this, "Failed to save settings", Toast.LENGTH_LONG).show();
                                    android.util.Log.e("StaffSettings", "Update error: " + error.getMessage());
                                }
                        );

                        RequestQueue updateQueue = Volley.newRequestQueue(this);
                        updateQueue.add(updateRequest);

                    } catch (JSONException e) {
                        Toast.makeText(this, "Error processing user data", Toast.LENGTH_SHORT).show();
                        android.util.Log.e("StaffSettings", "JSON error: " + e.getMessage());
                    }
                },
                error -> {
                    Toast.makeText(this, "Could not retrieve current user data", Toast.LENGTH_SHORT).show();
                    android.util.Log.e("StaffSettings", "Read error: " + error.getMessage());
                }
        );

        RequestQueue readQueue = Volley.newRequestQueue(this);
        readQueue.add(readRequest);
    }
}