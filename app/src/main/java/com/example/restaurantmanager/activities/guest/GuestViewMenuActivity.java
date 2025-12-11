package com.example.restaurantmanager.activities.guest;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.adapters.MenuAdapter;
import com.example.restaurantmanager.database.DatabaseHelper;
import com.example.restaurantmanager.models.MenuItem;
import java.util.ArrayList;
import java.util.List;

/**
 GuestViewMenuActivity - Displays menu items from database
 Design Pattern: MVC (this is the Controller)
 SOLID: Single Responsibility - handles menu display logic only
 */
public class GuestViewMenuActivity extends AppCompatActivity {

    // UI Components
    private ImageView backButton;
    private EditText searchBar;
    private RecyclerView menuRecyclerView;
    private TextView emptyStateText;
    private Button btnAllItems, btnStarters, btnPasta;

    // Data
    private DatabaseHelper databaseHelper;
    private List<MenuItem> allMenuItems;
    private List<MenuItem> filteredMenuItems;
    private MenuAdapter menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_view_menu);

        // Initialize database (Singleton pattern)
        databaseHelper = DatabaseHelper.getInstance(this);

        // Initialize views
        initializeViews();

        // Load menu items from database
        loadMenuItems();

        // Set up RecyclerView
        setupRecyclerView();

        // Set up listeners
        setupListeners();
    }

    //Initialize all view references
    private void initializeViews() {
        backButton = findViewById(R.id.backArrow);
        searchBar = findViewById(R.id.searchBar);
        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);

        // Category buttons
        btnAllItems = findViewById(R.id.btnAllItems);
        btnStarters = findViewById(R.id.btnStarters);
        btnPasta = findViewById(R.id.btnPasta);
    }

    //Load menu items from database
    private void loadMenuItems() {
        allMenuItems = databaseHelper.getAllMenuItems();
        filteredMenuItems = new ArrayList<>(allMenuItems);

        // Show/hide empty state
        if (allMenuItems.isEmpty()) {
            menuRecyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
        } else {
            menuRecyclerView.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
        }
    }

    /**
     * Set up RecyclerView with adapter and layout manager
     * Design Pattern: Adapter Pattern
     */
    private void setupRecyclerView() {
        menuAdapter = new MenuAdapter(this, filteredMenuItems);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuRecyclerView.setAdapter(menuAdapter);
    }

    //Set up all button and search listeners
    private void setupListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMenuItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Category filter buttons
        btnAllItems.setOnClickListener(v -> {
            filteredMenuItems = new ArrayList<>(allMenuItems);
            menuAdapter.updateMenuItems(filteredMenuItems);
            Toast.makeText(this, "Showing all items", Toast.LENGTH_SHORT).show();
        });

        btnStarters.setOnClickListener(v -> {
            // TODO: Implement category filtering in Phase 4
            Toast.makeText(this, "Starters filter - Coming soon!", Toast.LENGTH_SHORT).show();
        });

        btnPasta.setOnClickListener(v -> {
            // TODO: Implement category filtering in Phase 4
            Toast.makeText(this, "Pasta filter - Coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Filter menu items based on search query
     * @param query Search text
     */
    private void filterMenuItems(String query) {
        filteredMenuItems.clear();

        if (query.isEmpty()) {
            // Show all items if search is empty
            filteredMenuItems.addAll(allMenuItems);
        } else {
            // Filter by name or description
            String lowerCaseQuery = query.toLowerCase();
            for (MenuItem item : allMenuItems) {
                if (item.getName().toLowerCase().contains(lowerCaseQuery) ||
                        item.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                    filteredMenuItems.add(item);
                }
            }
        }

        // Update adapter
        menuAdapter.updateMenuItems(filteredMenuItems);

        // Show empty state if no results
        if (filteredMenuItems.isEmpty()) {
            emptyStateText.setText("No items found for '" + query + "'");
            emptyStateText.setVisibility(View.VISIBLE);
            menuRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            menuRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    //Refresh menu when returning to this activity
    @Override
    protected void onResume() {
        super.onResume();
        loadMenuItems();
        if (menuAdapter != null) {
            menuAdapter.updateMenuItems(filteredMenuItems);
        }
    }
}

