package com.example.restaurantmanager.activities.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 SessionManager - Manages user session data
 Design Pattern: Singleton
 SOLID: Single Responsibility - Only handles session management
 */
public class SessionManager {

    private static final String PREF_NAME = "RestaurantAppSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_TYPE = "usertype";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private static SessionManager instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Private constructor (Singleton pattern)
    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //Get singleton instance

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    //Create login session
    public void createLoginSession(String username, String userType) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_USER_TYPE, userType);
        editor.commit();
    }

    //Check if user is logged in
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    //Get logged in username
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    //Get user type (staff or guest)
    public String getUserType() {
        return sharedPreferences.getString(KEY_USER_TYPE, null);
    }

    //Logout user - clear session
    public void logout() {
        editor.clear();
        editor.commit();
    }
}