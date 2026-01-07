package com.example.restaurantmanager.adapters;

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
import java.util.List;


/**
 MenuAdapter - Adapter Pattern Implementation
 Converts MenuItem data into RecyclerView items with expandable descriptions
 SOLID: Single Responsibility - Only handles menu item display
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private Context context;
    private List<MenuItem> menuItems;

    public MenuAdapter(Context context, List<MenuItem> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);

        // Set item data
        holder.nameTextView.setText(item.getName());
        holder.priceTextView.setText(String.format("£%.2f", item.getPrice()));
        holder.descriptionTextView.setText(item.getDescription());

        // Load real image
        loadMenuItemImage(holder.menuItemImage, item.getImageUrl());

        // Initially hide description
        holder.descriptionTextView.setVisibility(View.GONE);
        holder.isExpanded = false;

        // Set up expand/collapse functionality
        holder.expandButton.setOnClickListener(v -> {
            holder.isExpanded = !holder.isExpanded;

            if (holder.isExpanded) {
                holder.descriptionTextView.setVisibility(View.VISIBLE);
                holder.expandButton.setRotation(180); // Rotate arrow up
            } else {
                holder.descriptionTextView.setVisibility(View.GONE);
                holder.expandButton.setRotation(0); // Rotate arrow down
            }
        });

        // Also expand/collapse when clicking the whole item row
        holder.itemView.setOnClickListener(v -> holder.expandButton.performClick());
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    /**
     Update the menu items list (for search/filter functionality)
     */
    public void updateMenuItems(List<MenuItem> newMenuItems) {
        this.menuItems = newMenuItems;
        notifyDataSetChanged();
    }

    /**
     ViewHolder - holds references to views for each item
     */
    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView menuItemImage;
        TextView nameTextView;
        TextView priceTextView;
        TextView descriptionTextView;
        ImageButton expandButton;
        boolean isExpanded = false;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            menuItemImage = itemView.findViewById(R.id.menuItemImage);
            nameTextView = itemView.findViewById(R.id.menuItemName);
            priceTextView = itemView.findViewById(R.id.menuItemPrice);
            descriptionTextView = itemView.findViewById(R.id.menuItemDescription);
            expandButton = itemView.findViewById(R.id.expandButton);
        }
    }

    private void loadMenuItemImage(ImageView imageView, String imagePath) {
        // Reset to placeholder first
        imageView.setImageResource(android.R.drawable.ic_menu_gallery);

        // Keep placeholder if no image
        if (imagePath == null || imagePath.isEmpty() || imagePath.equals("placeholder")) {
            return;
        }

        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                android.graphics.Bitmap bitmap =
                        android.graphics.BitmapFactory.decodeFile(imagePath);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("MenuAdapter", "Error loading image: " + e.getMessage());
        }
    }
}