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
import com.example.restaurantmanager.activities.guest.SuccessfulEditReservationActivity;
import com.example.restaurantmanager.activities.utils.NotificationHelper;
import com.example.restaurantmanager.database.DatabaseHelper;
import com.example.restaurantmanager.models.Reservation;
import java.util.Calendar;

/**
 * GuestEditReservationActivity - Edit existing reservations
 * SOLID: Single Responsibility - handles reservation editing only
 */
public class GuestEditReservationActivity extends AppCompatActivity {

    // UI Components (matching your XML IDs)
    private ImageView backButton;
    private Spinner numberOfPeopleSpinner;
    private EditText datePicker;
    private EditText timePicker;
    private TextView peopleWarning;
    private TextView dateWarning;
    private TextView timeWarning;
    private Button saveChangesButton;

    // Data
    private DatabaseHelper databaseHelper;
    private int reservationId;
    private String selectedDate = "";
    private String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reservation);

        // Initialize
        databaseHelper = DatabaseHelper.getInstance(this);

        initializeViews();
        setupSpinner();
        loadReservationData();
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
        saveChangesButton = findViewById(R.id.saveChangesButton);

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

    //Load existing reservation data from intent
    private void loadReservationData() {
        Intent intent = getIntent();
        reservationId = intent.getIntExtra("reservation_id", -1);
        selectedDate = intent.getStringExtra("reservation_date");
        selectedTime = intent.getStringExtra("reservation_time");
        int numberOfGuests = intent.getIntExtra("reservation_guests", 1);

        // Set values in UI
        if (selectedDate != null && !selectedDate.isEmpty()) {
            datePicker.setText(selectedDate);
        }

        if (selectedTime != null && !selectedTime.isEmpty()) {
            timePicker.setText(selectedTime);
        }

        // Set spinner to correct position
        numberOfPeopleSpinner.setSelection(numberOfGuests - 1);
    }

    //Set up button

    private void setupListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Date picker
        datePicker.setOnClickListener(v -> showDatePicker());

        // Time picker
        timePicker.setOnClickListener(v -> showTimePicker());

        // Save changes
        saveChangesButton.setOnClickListener(v -> saveChanges());
    }

    //Show date picker dialog
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        // If date already selected, parse it for initial value
        if (!selectedDate.isEmpty()) {
            String[] parts = selectedDate.split("-");
            if (parts.length == 3) {
                calendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    datePicker.setText(selectedDate);
                    dateWarning.setVisibility(View.INVISIBLE);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    //Show time picker dialog

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();

        // If time already selected, parse it for initial value
        if (!selectedTime.isEmpty()) {
            String[] parts = selectedTime.split(":");
            if (parts.length == 2) {
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
            }
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    timePicker.setText(selectedTime);
                    timeWarning.setVisibility(View.INVISIBLE);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );

        timePickerDialog.show();
    }

    //Validate and save changes

    private void saveChanges() {
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

        if (!isValid) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get existing reservation to preserve guest username and status
        Reservation existingReservation = databaseHelper.getReservationById(reservationId);
        if (existingReservation == null) {
            Toast.makeText(this, "Reservation not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Create updated reservation
        Reservation updatedReservation = new Reservation(
                reservationId,
                existingReservation.getGuestUsername(),
                selectedDate,
                selectedTime,
                numberOfGuests,
                existingReservation.getStatus()
        );

        // Update in database
        int result = databaseHelper.updateReservation(updatedReservation);

        if (result > 0) {
            NotificationHelper notificationHelper = NotificationHelper.getInstance(this);
            notificationHelper.sendStaffReservationChanged(updatedReservation);

            // Success - go to confirmation screen
            Intent intent = new Intent(this, SuccessfulEditReservationActivity.class);
            intent.putExtra("reservation_date", selectedDate);
            intent.putExtra("reservation_time", selectedTime);
            intent.putExtra("reservation_guests", numberOfGuests);
            startActivity(intent);
            finish();
        } else {
            // Error
            Toast.makeText(this, "Failed to update reservation. Please try again.", Toast.LENGTH_LONG).show();
        }
    }
}