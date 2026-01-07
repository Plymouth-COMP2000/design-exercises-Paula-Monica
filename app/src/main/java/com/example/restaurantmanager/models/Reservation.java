package com.example.restaurantmanager.models;

public class Reservation {
    private int id;
    private String guestUsername;
    private String date;           //Format: "2025-12-25"
    private String time;           //Format: "18:30"
    private int numberOfGuests;
    private String status;         //Format: "pending" "confirmed" "cancelled"

    //Constructor with ID (for existing reservations)
    public Reservation(int id, String guestUsername, String date, String time,
                       int numberOfGuests, String status) {
        this.id = id;
        this.guestUsername = guestUsername;
        this.date = date;
        this.time = time;
        this.numberOfGuests = numberOfGuests;
        this.status = status;
    }

    //Constructor without ID (for new reservations)
    public Reservation(String guestUsername, String date, String time,
                       int numberOfGuests, String status) {
        this.guestUsername = guestUsername;
        this.date = date;
        this.time = time;
        this.numberOfGuests = numberOfGuests;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public String getGuestUsername() { return guestUsername; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getNumberOfGuests() { return numberOfGuests; }
    public String getStatus() { return status; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setGuestUsername(String guestUsername) { this.guestUsername = guestUsername; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setNumberOfGuests(int numberOfGuests) { this.numberOfGuests = numberOfGuests; }
    public void setStatus(String status) { this.status = status; }
}
