package com.example.restaurantmanager.activities.staff;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.activities.utils.NotificationHelper;
import com.example.restaurantmanager.adapters.StaffReservationsAdapter;
import com.example.restaurantmanager.database.DatabaseHelper;
import com.example.restaurantmanager.models.Reservation;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 StaffManageReservationsActivity - Staff view/manage customer reservations
 Design Pattern: MVC (Controller)
 SOLID: Single Responsibility - handles reservation management UI only
 */
public class StaffManageReservationsActivity extends AppCompatActivity
        implements StaffReservationsAdapter.OnReservationActionListener {

    // UI Components
    private ImageView backArrow;
    private EditText searchBar;
    private Button btnAllReservations;
    private Button btnTodayReservations;
    private Button btnUpcomingReservations;
    private RecyclerView reservationsRecyclerView;
    private TextView emptyStateText;
    private Button btnCleanupCancelled;

    // Services
    private DatabaseHelper databaseHelper;
    private StaffReservationsAdapter adapter;

    // State
    private String currentFilter = "all";
    private String todayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_manage_reservations);

        // Initialize services
        databaseHelper = DatabaseHelper.getInstance(this);

        // Get today's date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        todayDate = dateFormat.format(new Date());

        initializeViews();
        setupRecyclerView();
        setupListeners();
        loadReservations();
    }

    //Initialize view components
    private void initializeViews() {
        backArrow = findViewById(R.id.backArrow);
        backArrow = findViewById(R.id.backArrow);
        searchBar = findViewById(R.id.searchBar);
        btnAllReservations = findViewById(R.id.btnAllReservations);
        btnTodayReservations = findViewById(R.id.btnTodayReservations);
        btnUpcomingReservations = findViewById(R.id.btnUpcomingReservations);
        reservationsRecyclerView = findViewById(R.id.reservationsRecyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);
        btnCleanupCancelled = findViewById(R.id.btnCleanupCancelled);
    }

    /**
     Setup RecyclerView with adapter
     Design Pattern: Adapter pattern
     */
    private void setupRecyclerView() {
        adapter = new StaffReservationsAdapter(this);
        reservationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reservationsRecyclerView.setAdapter(adapter);
    }

    //Setup click listeners and text watchers
    private void setupListeners() {
        // Back button
        backArrow.setOnClickListener(v -> finish());
        // Clean up button
        btnCleanupCancelled.setOnClickListener(v -> showCleanupDialog());

        // Search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
                updateEmptyState();
            }

            @Override
            public void afterTextChanged(Editable s) {}


        });

        // Filter buttons
        btnAllReservations.setOnClickListener(v -> applyFilter("all"));
        btnTodayReservations.setOnClickListener(v -> applyFilter("today"));
        btnUpcomingReservations.setOnClickListener(v -> applyFilter("upcoming"));
    }

    /**
     Load all reservations from database
     Design Pattern: Data access via Singleton DatabaseHelper
     */
    private void loadReservations() {
        List<Reservation> allReservations = databaseHelper.getAllReservations();
        adapter.setReservations(allReservations);
        updateEmptyState();
    }

    /**
     Apply date filter to reservations
     @param filter "all", "today", or "upcoming"
     */
    private void applyFilter(String filter) {
        android.util.Log.d("StaffManageRes", "===================");
        android.util.Log.d("StaffManageRes", "Apply filter: " + filter);

        currentFilter = filter;

        // Update button visuals
        resetFilterButtons();
        switch (filter) {
            case "all":
                btnAllReservations.setBackgroundTintList(
                        getResources().getColorStateList(android.R.color.darker_gray));
                break;
            case "today":
                btnTodayReservations.setBackgroundTintList(
                        getResources().getColorStateList(android.R.color.darker_gray));
                break;
            case "upcoming":
                btnUpcomingReservations.setBackgroundTintList(
                        getResources().getColorStateList(android.R.color.darker_gray));
                break;
        }

        // Clear search
        searchBar.setText("");

        // Reload everything from database
        List<Reservation> allReservations = databaseHelper.getAllReservations();
        android.util.Log.d("StaffManageRes", "Reloaded " + allReservations.size() + " reservations from DB");

        adapter.setReservations(allReservations);
        android.util.Log.d("StaffManageRes", "Set reservations to adapter");

        // Apply filter
        adapter.filterByDate(filter, todayDate);
        android.util.Log.d("StaffManageRes", "Filter applied, count: " + adapter.getItemCount());

        // Scroll to top to make change visible
        if (adapter.getItemCount() > 0) {
            reservationsRecyclerView.scrollToPosition(0);
        }

        updateEmptyState();
        android.util.Log.d("StaffManageRes", "===================");
    }

    //Reset all filter button colors

    private void resetFilterButtons() {
        int defaultColor = 0xFF94B1B6; // #94b1b6
        btnAllReservations.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(defaultColor));
        btnTodayReservations.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(defaultColor));
        btnUpcomingReservations.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(defaultColor));
    }

    //Show/hide empty state based on adapter count
    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            reservationsRecyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
        } else {
            reservationsRecyclerView.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
        }
    }

    /**
     Adapter callback - View reservation details
     SOLID: Interface Segregation - specific method for view action
     */
    @Override
    public void onViewDetails(Reservation reservation) {
        // Show detailed dialog
        new AlertDialog.Builder(this)
                .setTitle("Reservation Details")
                .setMessage(
                        "Guest: " + reservation.getGuestUsername() + "\n" +
                                "Date: " + reservation.getDate() + "\n" +
                                "Time: " + reservation.getTime() + "\n" +
                                "Guests: " + reservation.getNumberOfGuests() + "\n" +
                                "Status: " + reservation.getStatus() + "\n" +
                                "ID: #" + reservation.getId()
                )
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     Adapter callback - Cancel reservation
     SOLID: Single Responsibility - handles cancellation logic
     */
    @Override
    public void onCancelReservation(Reservation reservation) {
        // Confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation for "
                        + reservation.getGuestUsername() + "?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    // Update reservation status
                    reservation.setStatus("Cancelled");
                    int result = databaseHelper.updateReservation(reservation);

                    if (result > 0) {
                        Toast.makeText(this, "Reservation cancelled successfully",
                                Toast.LENGTH_SHORT).show();

                        if (result > 0) {
                            Toast.makeText(this, "Reservation cancelled successfully",
                                    Toast.LENGTH_SHORT).show();

                            // SEND NOTIFICATION TO GUEST
                            NotificationHelper notificationHelper = NotificationHelper.getInstance(this);
                            notificationHelper.sendGuestReservationCancelled(reservation);

                            // Reload data
                            loadReservations();
                            applyFilter(currentFilter);
                        }

                        // Reload data
                        loadReservations();
                        applyFilter(currentFilter);
                    } else {
                        Toast.makeText(this, "Failed to cancel reservation",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    //Reload data when returning to activity
    @Override
    protected void onResume() {
        super.onResume();
        loadReservations();
        applyFilter(currentFilter);
    }

    //Show confirmation dialog for cleaning up cancelled reservations
    private void showCleanupDialog() {
        // Count cancelled reservations
        List<Reservation> allReservations = databaseHelper.getAllReservations();
        int cancelledCount = 0;
        for (Reservation reservation : allReservations) {
            if (reservation.getStatus().equalsIgnoreCase("Cancelled")) {
                cancelledCount++;
            }
        }

        if (cancelledCount == 0) {
            Toast.makeText(this, "No cancelled reservations to clean up",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Clean Up Cancelled Reservations")
                .setMessage("This will permanently delete " + cancelledCount +
                        " cancelled reservation(s). This cannot be undone.\n\n" +
                        "Are you sure you want to continue?")
                .setPositiveButton("Yes, Delete", (dialog, which) -> performCleanup())
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     Perform the cleanup of cancelled reservations
     Design Pattern: Data access via Singleton DatabaseHelper
     SOLID: Single Responsibility - handles cleanup logic
     */
    private void performCleanup() {
        List<Reservation> allReservations = databaseHelper.getAllReservations();
        int deletedCount = 0;

        // Delete all cancelled reservations
        for (Reservation reservation : allReservations) {
            if (reservation.getStatus().equalsIgnoreCase("Cancelled")) {
                databaseHelper.deleteReservation(reservation.getId());
                deletedCount++;
            }
        }

        // Show success message
        Toast.makeText(this,
                "Successfully deleted " + deletedCount + " cancelled reservation(s)",
                Toast.LENGTH_LONG).show();

        // Reload the list
        loadReservations();
        applyFilter(currentFilter);

        android.util.Log.d("StaffManageRes", "Cleaned up " + deletedCount + " cancelled reservations");
    }
}
