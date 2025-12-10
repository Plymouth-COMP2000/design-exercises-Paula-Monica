package com.example.restaurantmanager.activities.staff;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.activities.common.WelcomeActivity;
import com.example.restaurantmanager.activities.utils.SessionManager;
import com.example.restaurantmanager.database.DatabaseHelper;
import com.example.restaurantmanager.models.Reservation;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 StaffDashboardActivity - Main dashboard for staff users
 Design Pattern: MVC (Controller)
 SOLID: Single Responsibility - handles staff dashboard UI and navigation
 */
public class StaffDashboardActivity extends AppCompatActivity {

    // UI Components
    private TextView welcomeMessage;
    private TextView greetingText;
    private TextView todayReservationsCount;
    private TextView menuItemsCount;
    private Button signOutButton;
    private Button settingsButton;
    private Button manageReservationsButton;
    private Button manageMenuButton;

    // Services
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        // Initialize services
        sessionManager = SessionManager.getInstance(this);
        databaseHelper = DatabaseHelper.getInstance(this);

        // Verify staff access
        if (!sessionManager.isLoggedIn() ||
                !sessionManager.getUserType().equalsIgnoreCase("staff")) {
            Toast.makeText(this, "Unauthorized access", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupListeners();
        loadDashboardData();
    }

    /**
     Initialize all view components
     SOLID: Single responsibility - UI initialization
     */
    private void initializeViews() {
        welcomeMessage = findViewById(R.id.welcomeMessage);
        greetingText = findViewById(R.id.greetingText);
        todayReservationsCount = findViewById(R.id.todayReservationsCount);
        menuItemsCount = findViewById(R.id.menuItemsCount);
        signOutButton = findViewById(R.id.signOutButton);
        settingsButton = findViewById(R.id.settingsButton);
        manageReservationsButton = findViewById(R.id.manageReservationsButton);
        manageMenuButton = findViewById(R.id.manageMenuButton);

        // Set personalized greeting
        String username = sessionManager.getUsername();
        greetingText.setText("Welcome back, " + username + "!");
    }

    /**
     Setup click listeners for all interactive elements
     SOLID: Single responsibility - event handling setup
     */
    private void setupListeners() {
        // Sign out button
        signOutButton.setOnClickListener(v -> showSignOutDialog());

        // Settings button
        //settingsButton.setOnClickListener(v -> {
            //Intent intent = new Intent(this, StaffSettingsActivity.class);
            //startActivity(intent);
       // });

        // Manage reservations button
        //manageReservationsButton.setOnClickListener(v -> {
            //Intent intent = new Intent(this, StaffManageReservationsActivity.class);
            //startActivity(intent);
       // });

        // Manage menu button
       // manageMenuButton.setOnClickListener(v -> {
           // Intent intent = new Intent(this, StaffManageMenuActivity.class);
            //startActivity(intent);
        //});
    }

    /**
     Load dashboard statistics
     Design Pattern: Data access through DatabaseHelper (Singleton)
     */
    private void loadDashboardData() {
        // Get today's date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        String todayDate = dateFormat.format(new Date());

        // Get today's reservations count
        List<Reservation> allReservations = databaseHelper.getAllReservations();
        int todayCount = 0;
        for (Reservation reservation : allReservations) {
            if (reservation.getDate().equals(todayDate)) {
                todayCount++;
            }
        }
        todayReservationsCount.setText(String.valueOf(todayCount));

        // Get total menu items count
        int menuCount = databaseHelper.getAllMenuItems().size();
        menuItemsCount.setText(String.valueOf(menuCount));
    }

    /**
     Show sign out confirmation dialog
     UX: Prevents accidental sign out
     */
    private void showSignOutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes", (dialog, which) -> performSignOut())
                .setNegativeButton("No", null)
                .show();
    }

    /**
     Perform sign out operation
     Design Pattern: SessionManager handles logout logic
     */
    private void performSignOut() {
        sessionManager.logout();
        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();

        // Navigate to welcome screen
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    //Refresh dashboard data when returning to activity

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }
}