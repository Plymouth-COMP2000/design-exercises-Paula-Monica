package com.example.restaurantmanager.activities.staff;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.adapters.StaffMenuAdapter;
import com.example.restaurantmanager.database.DatabaseHelper;
import com.example.restaurantmanager.models.MenuItem;
import java.util.List;

/**
 StaffManageMenuActivity - Staff manage menu items
 Design Pattern: MVC (Controller)
 SOLID: Single Responsibility - handles menu management UI
 */
public class StaffManageMenuActivity extends AppCompatActivity
        implements StaffMenuAdapter.OnMenuActionListener {

    // UI Components
    private ImageView backArrow;
    private Button addNewItemButton;
    private RecyclerView menuRecyclerView;
    private TextView emptyStateText;

    // Services
    private DatabaseHelper databaseHelper;
    private StaffMenuAdapter adapter;

    // Request codes
    private static final int REQUEST_ADD_ITEM = 1;
    private static final int REQUEST_EDIT_ITEM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_manage_menu);

        // Initialize services
        databaseHelper = DatabaseHelper.getInstance(this);

        initializeViews();
        setupRecyclerView();
        setupListeners();
        loadMenuItems();
    }

    //Initialize view components

    private void initializeViews() {
        backArrow = findViewById(R.id.backArrow);
        addNewItemButton = findViewById(R.id.addNewItemButton);
        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);
    }

    /**
     Setup RecyclerView with adapter
     Design Pattern: Adapter pattern
     */
    private void setupRecyclerView() {
        adapter = new StaffMenuAdapter(this, this);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuRecyclerView.setAdapter(adapter);
    }

    //Setup click listeners

    private void setupListeners() {
        backArrow.setOnClickListener(v -> finish());

        addNewItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, StaffAddEditMenuItemActivity.class);
            startActivityForResult(intent, REQUEST_ADD_ITEM);
        });
    }

    //Load menu items from database
    private void loadMenuItems() {
        List<MenuItem> menuItems = databaseHelper.getAllMenuItems();
        adapter.setMenuItems(menuItems);
        updateEmptyState();
    }

    //Show/hide empty state

    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            menuRecyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
        } else {
            menuRecyclerView.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
        }
    }

    //Adapter callback - Edit menu item
    @Override
    public void onEdit(MenuItem item) {
        Intent intent = new Intent(this, StaffAddEditMenuItemActivity.class);
        intent.putExtra("ITEM_ID", item.getId());
        intent.putExtra("ITEM_NAME", item.getName());
        intent.putExtra("ITEM_PRICE", item.getPrice());
        intent.putExtra("ITEM_DESCRIPTION", item.getDescription());
        intent.putExtra("ITEM_IMAGE", item.getImageUrl());
        intent.putExtra("ITEM_CATEGORY", item.getCategory());
        startActivityForResult(intent, REQUEST_EDIT_ITEM);
    }

    //Adapter callback - Delete menu item
    @Override
    public void onDelete(MenuItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Menu Item")
                .setMessage("Are you sure you want to delete '" + item.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseHelper.deleteMenuItem(item.getId());
                    Toast.makeText(this, "Menu item deleted", Toast.LENGTH_SHORT).show();
                    loadMenuItems();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    //Handle results from Add/Edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_ITEM) {
                Toast.makeText(this, "Menu item added successfully", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_EDIT_ITEM) {
                Toast.makeText(this, "Menu item updated successfully", Toast.LENGTH_SHORT).show();
            }
            loadMenuItems();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMenuItems();
    }
}
