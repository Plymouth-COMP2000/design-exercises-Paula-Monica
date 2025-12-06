package com.example.restaurantmanager.activities.guest;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantmanager.R;

public class GuestViewMenuActivity extends AppCompatActivity {
    private ImageView backButton;
    private EditText searchBar;
    private TextView itemDescriptionBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_view_menu);

        backButton = findViewById(R.id.backArrow);
        searchBar = findViewById(R.id.searchBar);
        itemDescriptionBox = findViewById(R.id.itemDescriptionBox);

    }
}