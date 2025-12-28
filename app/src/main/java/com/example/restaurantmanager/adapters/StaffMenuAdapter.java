package com.example.restaurantmanager.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.models.MenuItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 StaffMenuAdapter - Adapter for staff menu management
 Design Pattern: Adapter pattern
 SOLID: Single Responsibility - manages menu item views for staff
 */
public class StaffMenuAdapter extends RecyclerView.Adapter<StaffMenuAdapter.MenuViewHolder> {

    private List<MenuItem> menuItems;
    private Context context;
    private OnMenuActionListener listener;

    /**
     Interface for menu item actions
     SOLID: Interface Segregation - specific callbacks for staff actions
     */
    public interface OnMenuActionListener {
        void onEdit(MenuItem item);
        void onDelete(MenuItem item);
    }

    public StaffMenuAdapter(Context context, OnMenuActionListener listener) {
        this.context = context;
        this.menuItems = new ArrayList<>();
        this.listener = listener;
    }

    //Update adapter with new data
    @SuppressLint("NotifyDataSetChanged")
    public void setMenuItems(List<MenuItem> items) {
        this.menuItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_staff_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    //ViewHolder for menu items
    class MenuViewHolder extends RecyclerView.ViewHolder {

        private final ImageView menuItemImage;
        private final TextView menuItemName;
        private final TextView menuItemDescription;
        private final TextView menuItemPrice;
        private ImageButton expandButton;


        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);

            menuItemImage = itemView.findViewById(R.id.menuItemImage);
            menuItemName = itemView.findViewById(R.id.menuItemName);
            menuItemDescription = itemView.findViewById(R.id.menuItemDescription);
            menuItemPrice = itemView.findViewById(R.id.menuItemPrice);
        }

        public void bind(MenuItem item) {
            // Set item details
            menuItemName.setText(item.getName());
            menuItemDescription.setText(item.getDescription());
            menuItemPrice.setText(String.format(Locale.UK, "£%.2f", item.getPrice()));

            // Load real image
            loadMenuItemImage(item.getImageUrl());

            ImageButton btnEdit = itemView.findViewById(R.id.btnEdit);
            ImageButton btnDelete = itemView.findViewById(R.id.btnDelete);

            //Edit button click
            btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(item);
            });

            //Delete button click
            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(item);
            });
        }

        //Load menu item image from file path or use placeholder
        private void loadMenuItemImage(String imagePath) {

            // No image then keep the XML placeholder image
            if (imagePath == null || imagePath.isEmpty() || imagePath.equals("placeholder")) {
                return; // Don't override the ImageView's default XML image
            }

            // Try to load from internal storage
            try {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(imagePath);
                    if (bitmap != null) {
                        menuItemImage.setImageBitmap(bitmap);
                        return;
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("StaffMenuAdapter", "Error loading image: " + e.getMessage());
            }
        }


        }

    }

