package com.example.restaurantmanager.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.models.Reservation;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 StaffReservationAdapter - Adapter for staff reservation list
 Design Pattern: Adapter pattern (Android RecyclerView)
 SOLID: Single Responsibility - manages reservation item views only
 */
public class StaffReservationsAdapter extends RecyclerView.Adapter<StaffReservationsAdapter.ReservationViewHolder> {

    private List<Reservation> reservations;
    private List<Reservation> reservationsFiltered; // For search/filter
    private OnReservationActionListener listener;

    /**
     Interface for handling reservation actions
     SOLID: Interface Segregation - specific callbacks for staff actions
     */
    public interface OnReservationActionListener {
        void onViewDetails(Reservation reservation);
        void onCancelReservation(Reservation reservation);
    }

    public StaffReservationsAdapter(OnReservationActionListener listener) {
        this.reservations = new ArrayList<>();
        this.reservationsFiltered = new ArrayList<>();
        this.listener = listener;
    }

    /**
     Update adapter data
     @param newReservations Updated list of reservations
     */
    public void setReservations(List<Reservation> newReservations) {
        this.reservations = newReservations;
        this.reservationsFiltered = new ArrayList<>(newReservations);
        notifyDataSetChanged();
    }

    /**
     Filter reservations by guest name
     @param query Search query
     */
    public void filter(String query) {
        reservationsFiltered.clear();

        if (query.isEmpty()) {
            reservationsFiltered.addAll(reservations);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Reservation reservation : reservations) {
                if (reservation.getGuestUsername().toLowerCase().contains(lowerCaseQuery)) {
                    reservationsFiltered.add(reservation);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     Filter by date criteria
     @param filter "all", "today", "upcoming"
     */
    @SuppressLint("LongLogTag")
    public void filterByDate(String filter, String todayDate) {
        android.util.Log.d("StaffReservationsAdapter", "=== FILTER CALLED ===");
        android.util.Log.d("StaffReservationsAdapter", "Filter type: " + filter);
        android.util.Log.d("StaffReservationsAdapter", "Today's date: " + todayDate);
        android.util.Log.d("StaffReservationsAdapter", "Total reservations: " + reservations.size());

        reservationsFiltered.clear();

        if (filter.equals("all")) {
            // Show all reservations
            reservationsFiltered.addAll(reservations);
            android.util.Log.d("StaffReservationsAdapter", "Showing ALL: " + reservationsFiltered.size());

        } else if (filter.equals("today")) {
            // Show only today's reservations
            for (Reservation reservation : reservations) {
                boolean isToday = reservation.getDate().equals(todayDate);
                android.util.Log.d("StaffReservationsAdapter", "  " + reservation.getGuestUsername() +
                        " - Date: " + reservation.getDate() +
                        " - IsToday: " + isToday);

                if (isToday) {
                    reservationsFiltered.add(reservation);
                }
            }
            android.util.Log.d("StaffReservationsAdapter", "Showing TODAY: " + reservationsFiltered.size());

        } else if (filter.equals("upcoming")) {
            // Show future reservations (dates after today)
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
                Date today = sdf.parse(todayDate);

                for (Reservation reservation : reservations) {
                    try {
                        Date reservationDate = sdf.parse(reservation.getDate());

                        // Check if reservation date is AFTER today
                        boolean isFuture = reservationDate != null && reservationDate.after(today);

                        android.util.Log.d("StaffReservationsAdapter", "  " + reservation.getGuestUsername() +
                                " - Date: " + reservation.getDate() +
                                " - IsFuture: " + isFuture);

                        if (isFuture) {
                            reservationsFiltered.add(reservation);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("StaffReservationsAdapter", "Error parsing date: " + reservation.getDate());
                    }
                }
                android.util.Log.d("StaffReservationsAdapter", "Showing UPCOMING: " + reservationsFiltered.size());

            } catch (Exception e) {
                android.util.Log.e("StaffReservationsAdapter", "Error parsing today's date", e);
                // Fallback: show all except today
                for (Reservation reservation : reservations) {
                    if (!reservation.getDate().equals(todayDate)) {
                        reservationsFiltered.add(reservation);
                    }
                }
            }
        }

        android.util.Log.d("StaffReservationsAdapter", "Final filtered count: " + reservationsFiltered.size());
        android.util.Log.d("StaffReservationsAdapter", "=====================");

        notifyDataSetChanged();
        android.util.Log.d("StaffReservationsAdapter", "notifyDataSetChanged() called");
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_staff_reservations, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservationsFiltered.get(position);
        holder.bind(reservation);
    }

    @Override
    public int getItemCount() {
        return reservationsFiltered.size();
    }

    /**
     ViewHolder for reservation items
     SOLID: Single Responsibility - manages individual item view binding
     */
    class ReservationViewHolder extends RecyclerView.ViewHolder {
        private TextView guestName;
        private TextView reservationStatus;
        private TextView reservationDate;
        private TextView reservationTime;
        private TextView numberOfGuests;
        private Button viewDetailsButton;
        private Button cancelButton;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            guestName = itemView.findViewById(R.id.guestName);
            reservationStatus = itemView.findViewById(R.id.reservationStatus);
            reservationDate = itemView.findViewById(R.id.reservationDate);
            reservationTime = itemView.findViewById(R.id.reservationTime);
            numberOfGuests = itemView.findViewById(R.id.numberOfGuests);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }

        public void bind(Reservation reservation) {
            // Set reservation details
            guestName.setText(reservation.getGuestUsername());
            reservationStatus.setText(reservation.getStatus());
            reservationDate.setText(reservation.getDate());
            reservationTime.setText(reservation.getTime());
            numberOfGuests.setText(reservation.getNumberOfGuests() + " guests");

            // Change status color based on status
            switch (reservation.getStatus().toLowerCase()) {
                case "confirmed":
                    reservationStatus.setBackgroundColor(0xFFEDC099); // #edc099
                    break;
                case "cancelled":
                    reservationStatus.setBackgroundColor(0xFFE5A975); // #e5a975
                    reservationStatus.setTextColor(0xFFFFFFFF); // White text
                    break;
                default:
                    reservationStatus.setBackgroundColor(0xFF94B1B6); // #94b1b6
                    break;
            }

            // Hide cancel button if already cancelled
            if (reservation.getStatus().equalsIgnoreCase("cancelled")) {
                cancelButton.setVisibility(View.GONE);
            } else {
                cancelButton.setVisibility(View.VISIBLE);
            }

            // Set click listeners
            viewDetailsButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewDetails(reservation);
                }
            });

            cancelButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelReservation(reservation);
                }
            });
        }
    }
}
