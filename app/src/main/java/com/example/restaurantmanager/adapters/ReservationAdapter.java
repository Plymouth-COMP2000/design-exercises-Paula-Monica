package com.example.restaurantmanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.activities.guest.GuestEditReservationActivity;
import com.example.restaurantmanager.models.Reservation;
import java.util.List;

/**
 ReservationAdapter - Adapter Pattern Implementation
 Displays reservation items in a RecyclerView
 SOLID: Single Responsibility - Only handles reservation display
 */
public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private Context context;
    private List<Reservation> reservations;
    private OnReservationDeleteListener deleteListener;

    // Interface for delete callback
    public interface OnReservationDeleteListener {
        void onReservationDelete(Reservation reservation, int position);
    }

    public ReservationAdapter(Context context, List<Reservation> reservations, OnReservationDeleteListener deleteListener) {
        this.context = context;
        this.reservations = reservations;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);

        // Set reservation data
        holder.dateTextView.setText("Date: " + reservation.getDate());
        holder.timeTextView.setText("Time: " + reservation.getTime());
        holder.guestsTextView.setText("Guests: " + reservation.getNumberOfGuests());
        holder.statusTextView.setText("Status: " + reservation.getStatus());

        // Set status color
        if (reservation.getStatus().equalsIgnoreCase("confirmed")) {
            holder.statusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else if (reservation.getStatus().equalsIgnoreCase("pending")) {
            holder.statusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        } else if (reservation.getStatus().equalsIgnoreCase("cancelled")) {
            holder.statusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        // Edit button - opens edit activity
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, GuestEditReservationActivity.class);
            intent.putExtra("reservation_id", reservation.getId());
            intent.putExtra("reservation_date", reservation.getDate());
            intent.putExtra("reservation_time", reservation.getTime());
            intent.putExtra("reservation_guests", reservation.getNumberOfGuests());
            intent.putExtra("reservation_status", reservation.getStatus());
            context.startActivity(intent);
        });

        // Cancel button - deletes reservation
        holder.cancelButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onReservationDelete(reservation, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    //Update reservations list
    public void updateReservations(List<Reservation> newReservations) {
        this.reservations = newReservations;
        notifyDataSetChanged();
    }

    //Remove item from list
    public void removeItem(int position) {
        reservations.remove(position);
        notifyItemRemoved(position);
    }

    //ViewHolder - holds references to reservation item views
    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView timeTextView;
        TextView guestsTextView;
        TextView statusTextView;
        ImageView editButton;
        ImageView cancelButton;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.reservationDate);
            timeTextView = itemView.findViewById(R.id.reservationTime);
            guestsTextView = itemView.findViewById(R.id.reservationGuests);
            statusTextView = itemView.findViewById(R.id.reservationStatus);
            editButton = itemView.findViewById(R.id.editReservationButton);
            cancelButton = itemView.findViewById(R.id.cancelReservationButton);
        }
    }
}
