package com.example.restaurantmanager.activities.guest;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.activities.guest.SuccessfulReservationActivity;
import com.example.restaurantmanager.database.DatabaseHelper;
import com.example.restaurantmanager.models.Reservation;
import com.example.restaurantmanager.activities.utils.SessionManager;
import java.util.Calendar;

/**
 GuestMakeReservationActivity - Create new reservations
 SOLID: Single Responsibility - handles reservation creation only
 */
public class GuestMakeReservationActivity extends AppCompatActivity {

    // UI Components
    private ImageView backButton;
    private Spinner numberOfPeopleSpinner;
    private EditText datePicker;
    private EditText timePicker;
    private TextView peopleWarning;
    private TextView dateWarning;
    private TextView timeWarning;
    private Button confirmButton;

    // Data
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private String selectedDate = "";
    private String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_make_reservation);

        // Initialize
        databaseHelper = DatabaseHelper.getInstance(this);
        sessionManager = SessionManager.getInstance(this);

        initializeViews();
        setupSpinner();
        setupListeners();
    }

    //Initialize view references
    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        numberOfPeopleSpinner = findViewById(R.id.numberOfPeopleSpinner);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        peopleWarning = findViewById(R.id.peopleWarning);
        dateWarning = findViewById(R.id.dateWarning);
        timeWarning = findViewById(R.id.timeWarning);
        confirmButton = findViewById(R.id.confirmButton);

        // Make date and time pickers non-editable
        datePicker.setFocusable(false);
        datePicker.setClickable(true);
        timePicker.setFocusable(false);
        timePicker.setClickable(true);

        // Hide warnings initially
        peopleWarning.setVisibility(View.INVISIBLE);
        dateWarning.setVisibility(View.INVISIBLE);
        timeWarning.setVisibility(View.INVISIBLE);
    }

    //Set up number of people spinner

    private void setupSpinner() {
        // Create array of guest counts (1-20)
        String[] guestCounts = new String[20];
        for (int i = 0; i < 20; i++) {
            guestCounts[i] = String.valueOf(i + 1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                guestCounts
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberOfPeopleSpinner.setAdapter(adapter);
    }

    //Set up button listeners

    private void setupListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Date picker
        datePicker.setOnClickListener(v -> showDatePicker());

        // Time picker
        timePicker.setOnClickListener(v -> showTimePicker());

        // Confirm reservation
        confirmButton.setOnClickListener(v -> confirmReservation());
    }

    //Show date picker dialog

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format: 2025-12-25
                    selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    datePicker.setText(selectedDate);
                    dateWarning.setVisibility(View.INVISIBLE); // Hide warning when date selected
                },
                year, month, day
        );

        // Don't allow past dates
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    //Show time picker dialog
    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    // Format: 19:30
                    selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    timePicker.setText(selectedTime);
                    timeWarning.setVisibility(View.INVISIBLE); // Hide warning when time selected
                },
                hour, minute, true // 24-hour format
        );

        timePickerDialog.show();
    }

    //Validate inputs and create reservation
    private void confirmReservation() {
        boolean isValid = true;

        // Validate number of guests
        int numberOfGuests = Integer.parseInt(numberOfPeopleSpinner.getSelectedItem().toString());
        if (numberOfGuests <= 0) {
            peopleWarning.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            peopleWarning.setVisibility(View.INVISIBLE);
        }

        // Validate date
        if (selectedDate.isEmpty() || datePicker.getText().toString().isEmpty()) {
            dateWarning.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            dateWarning.setVisibility(View.INVISIBLE);
        }

        // Validate time
        if (selectedTime.isEmpty() || timePicker.getText().toString().isEmpty()) {
            timeWarning.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            timeWarning.setVisibility(View.INVISIBLE);
        }

        // If validation fails, stop here
        if (!isValid) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get logged in username
        String username = sessionManager.getUsername();
        if (username == null || username.isEmpty()) {
            // Fallback for testing
            username = "guest_user";
        }

        // Create reservation object
        Reservation reservation = new Reservation(
                username,
                selectedDate,
                selectedTime,
                numberOfGuests,
                "confirmed" // Default status
        );

        // Save to database
        long result = databaseHelper.addReservation(reservation);

        if (result != -1) {
            // Success - go to confirmation screen
            Intent intent = new Intent(this, SuccessfulReservationActivity.class);
            intent.putExtra("reservation_date", selectedDate);
            intent.putExtra("reservation_time", selectedTime);
            intent.putExtra("reservation_guests", numberOfGuests);
            startActivity(intent);
            finish(); // Close this activity
        } else {
            // Error
            Toast.makeText(this, "Failed to create reservation. Please try again.", Toast.LENGTH_LONG).show();
        }
    }
}
