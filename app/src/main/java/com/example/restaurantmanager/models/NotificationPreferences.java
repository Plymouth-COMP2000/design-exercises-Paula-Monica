package com.example.restaurantmanager.models;

import android.content.Context;
import android.content.SharedPreferences;

/**
 NotificationPreferences - Model for storing user notification settings
 Design Pattern: Singleton
 SOLID: Single Responsibility - Only handles notification preference storage
 */
public class NotificationPreferences {

    private static final String PREF_NAME = "NotificationPreferences";
    private static NotificationPreferences instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Guest notification keys
    private static final String KEY_GUEST_CANCELLATION = "guest_cancellation";
    private static final String KEY_GUEST_DAY_BEFORE = "guest_day_before";
    private static final String KEY_GUEST_HOUR_BEFORE = "guest_hour_before";

    // Staff notification keys
    private static final String KEY_STAFF_NEW_RESERVATION = "staff_new_reservation";
    private static final String KEY_STAFF_RESERVATION_CHANGES = "staff_reservation_changes";
    private static final String KEY_STAFF_15_MIN_BEFORE = "staff_15_min_before";
    private static final String KEY_STAFF_30_MIN_BEFORE = "staff_30_min_before";

    //Private constructor (Singleton pattern)
    private NotificationPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setDefaultPreferences();
    }

    /**
     Get singleton instance
     @param context Application context
     @return Single NotificationPreferences instance
     */
    public static synchronized NotificationPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationPreferences(context.getApplicationContext());
        }
        return instance;
    }

    //Set default preferences (all enabled) on first launch
    private void setDefaultPreferences() {
        if (!sharedPreferences.contains(KEY_GUEST_CANCELLATION)) {
            // Guest defaults (all enabled)
            editor.putBoolean(KEY_GUEST_CANCELLATION, true);
            editor.putBoolean(KEY_GUEST_DAY_BEFORE, true);
            editor.putBoolean(KEY_GUEST_HOUR_BEFORE, true);

            // Staff defaults (all enabled)
            editor.putBoolean(KEY_STAFF_NEW_RESERVATION, true);
            editor.putBoolean(KEY_STAFF_RESERVATION_CHANGES, true);
            editor.putBoolean(KEY_STAFF_15_MIN_BEFORE, true);
            editor.putBoolean(KEY_STAFF_30_MIN_BEFORE, true);

            editor.apply();
        }
    }

    //GUEST PREFERENCES

    //Check if guest cancellation notifications are enabled
    public boolean isGuestCancellationEnabled() {
        return sharedPreferences.getBoolean(KEY_GUEST_CANCELLATION, true);
    }

    //Set guest cancellation notification preference
    public void setGuestCancellationEnabled(boolean enabled) {
        editor.putBoolean(KEY_GUEST_CANCELLATION, enabled);
        editor.apply();
    }

    //Check if day before reminder is enabled
    public boolean isGuestDayBeforeEnabled() {
        return sharedPreferences.getBoolean(KEY_GUEST_DAY_BEFORE, true);
    }

    //Set day before reminder preference
    public void setGuestDayBeforeEnabled(boolean enabled) {
        editor.putBoolean(KEY_GUEST_DAY_BEFORE, enabled);
        editor.apply();
    }

    //Check if hour before reminder is enabled
    public boolean isGuestHourBeforeEnabled() {
        return sharedPreferences.getBoolean(KEY_GUEST_HOUR_BEFORE, true);
    }

    //Set hour before reminder preference
    public void setGuestHourBeforeEnabled(boolean enabled) {
        editor.putBoolean(KEY_GUEST_HOUR_BEFORE, enabled);
        editor.apply();
    }

    //STAFF PREFERENCES

    //Check if new reservation notifications are enabled
    public boolean isStaffNewReservationEnabled() {
        return sharedPreferences.getBoolean(KEY_STAFF_NEW_RESERVATION, true);
    }

    //Set new reservation notification preference
    public void setStaffNewReservationEnabled(boolean enabled) {
        editor.putBoolean(KEY_STAFF_NEW_RESERVATION, enabled);
        editor.apply();
    }

    //Check if reservation change notifications are enabled
    public boolean isStaffReservationChangesEnabled() {
        return sharedPreferences.getBoolean(KEY_STAFF_RESERVATION_CHANGES, true);
    }

    //Set reservation change notification preference
    public void setStaffReservationChangesEnabled(boolean enabled) {
        editor.putBoolean(KEY_STAFF_RESERVATION_CHANGES, enabled);
        editor.apply();
    }

    //Check if 15 minutes before reminder is enabled
    public boolean isStaff15MinBeforeEnabled() {
        return sharedPreferences.getBoolean(KEY_STAFF_15_MIN_BEFORE, true);
    }

    //Set 15 minutes before reminder preference
    public void setStaff15MinBeforeEnabled(boolean enabled) {
        editor.putBoolean(KEY_STAFF_15_MIN_BEFORE, enabled);
        editor.apply();
    }

    //Check if 30 minutes before reminder is enabled
    public boolean isStaff30MinBeforeEnabled() {
        return sharedPreferences.getBoolean(KEY_STAFF_30_MIN_BEFORE, true);
    }

    //Set 30 minutes before reminder preference
    public void setStaff30MinBeforeEnabled(boolean enabled) {
        editor.putBoolean(KEY_STAFF_30_MIN_BEFORE, enabled);
        editor.apply();
    }

    //Clear all preferences (for testing/logout)
    public void clearAll() {
        editor.clear();
        editor.apply();
        setDefaultPreferences();
    }
}
