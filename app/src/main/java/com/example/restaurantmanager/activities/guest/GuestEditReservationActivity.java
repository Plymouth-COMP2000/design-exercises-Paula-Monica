package com.example.restaurantmanager.activities.guest;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantmanager.R;

public class GuestEditReservationActivity extends AppCompatActivity {

    private ImageView backButton;
    private Spinner numberOfPeopleSpinner;
    private EditText datePicker;
    private EditText timePicker;
    private TextView peopleWarning;
    private TextView dateWarning;
    private TextView timeWarning;
    private Button saveChangesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reservation);

        backButton = findViewById(R.id.backButton);
        numberOfPeopleSpinner = findViewById(R.id.numberOfPeopleSpinner);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        peopleWarning = findViewById(R.id.peopleWarning);
        dateWarning = findViewById(R.id.dateWarning);
        timeWarning = findViewById(R.id.timeWarning);
        saveChangesButton = findViewById(R.id.saveChangesButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.number_of_people_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberOfPeopleSpinner.setAdapter(adapter);

    }
}