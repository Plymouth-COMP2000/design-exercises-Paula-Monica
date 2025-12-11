package com.example.restaurantmanager.activities.guest;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.adapters.ReservationAdapter;
import com.example.restaurantmanager.database.DatabaseHelper;
import com.example.restaurantmanager.models.Reservation;
import com.example.restaurantmanager.activities.utils.SessionManager;
import java.util.List;

/**
 GuestMyReservationActivity - Display user's reservations
 SOLID: Single Responsibility - handles reservation display only
 */
public class MyReservationsActivity extends AppCompatActivity implements ReservationAdapter.OnReservationDeleteListener {

    // UI Components
    private ImageView backButton;
    private RecyclerView reservationsRecyclerView;
    private TextView emptyStateText;

    // Data
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private List<Reservation> reservations;
    private ReservationAdapter reservationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservation);

        // Initialize
        databaseHelper = DatabaseHelper.getInstance(this);
        sessionManager = SessionManager.getInstance(this);

        initializeViews();
        loadReservations();
        setupRecyclerView();
        setupListeners();
    }

    //Initialize view references
    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        reservationsRecyclerView = findViewById(R.id.reservationsRecyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);
    }

    //Load reservations from database for current user
    private void loadReservations() {
        String username = sessionManager.getUsername();
        if (username == null || username.isEmpty()) {
            username = "guest_user"; // Fallback for testing
        }

        reservations = databaseHelper.getReservationsByGuest(username);

        // Show/hide empty state
        if (reservations.isEmpty()) {
            reservationsRecyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
        } else {
            reservationsRecyclerView.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
        }
    }

    /**
     Set up RecyclerView with adapter
     Design Pattern: Adapter Pattern
     */
    private void setupRecyclerView() {
        reservationAdapter = new ReservationAdapter(this, reservations, this);
        reservationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reservationsRecyclerView.setAdapter(reservationAdapter);
    }

    //Set up button

    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());
    }

    //Handle reservation deletion

    @Override
    public void onReservationDelete(Reservation reservation, int position) {
        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Delete from database
                    databaseHelper.deleteReservation(reservation.getId());

                    // Remove from adapter
                    reservationAdapter.removeItem(position);

                    // Show empty state if no more reservations
                    if (reservations.isEmpty()) {
                        reservationsRecyclerView.setVisibility(View.GONE);
                        emptyStateText.setVisibility(View.VISIBLE);
                    }

                    Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    //Refresh reservations when returning from edit screen

    @Override
    protected void onResume() {
        super.onResume();
        loadReservations();
        if (reservationAdapter != null) {
            reservationAdapter.updateReservations(reservations);
        }
    }
}