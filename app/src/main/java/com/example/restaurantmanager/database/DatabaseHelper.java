package com.example.restaurantmanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.restaurantmanager.models.MenuItem;
import com.example.restaurantmanager.models.Reservation;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database configuration
    private static final String DATABASE_NAME = "RestaurantManager.db";
    private static final int DATABASE_VERSION = 2;

    // Table names
    private static final String TABLE_MENU = "menu_items";
    private static final String TABLE_RESERVATIONS = "reservations";

    // Menu table columns
    private static final String KEY_MENU_ID = "id";
    private static final String KEY_MENU_NAME = "name";
    private static final String KEY_MENU_PRICE = "price";
    private static final String KEY_MENU_IMAGE = "image_url";
    private static final String KEY_MENU_DESC = "description";
    private static final String KEY_MENU_CATEGORY = "category";

    // Reservations table columns
    private static final String KEY_RES_ID = "id";
    private static final String KEY_RES_GUEST = "guest_username";
    private static final String KEY_RES_DATE = "date";
    private static final String KEY_RES_TIME = "time";
    private static final String KEY_RES_GUESTS = "number_of_guests";
    private static final String KEY_RES_STATUS = "status";

    // Singleton instance
    private static DatabaseHelper instance;

    /**
     Private constructor prevents direct instantiation
     Part of Singleton pattern
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     Get singleton instance - thread-safe
     Design Pattern: Singleton
     @param context Application context
     @return Single DatabaseHelper instance
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Menu Items table
        String CREATE_MENU_TABLE = "CREATE TABLE " + TABLE_MENU + "("
                + KEY_MENU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_MENU_NAME + " TEXT NOT NULL,"
                + KEY_MENU_PRICE + " REAL NOT NULL,"
                + KEY_MENU_IMAGE + " TEXT,"
                + KEY_MENU_DESC + " TEXT,"
                + KEY_MENU_CATEGORY + " TEXT DEFAULT 'Other'"
                + ")";
        db.execSQL(CREATE_MENU_TABLE);

        // Create Reservations table
        String CREATE_RES_TABLE = "CREATE TABLE " + TABLE_RESERVATIONS + "("
                + KEY_RES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_RES_GUEST + " TEXT NOT NULL,"
                + KEY_RES_DATE + " TEXT NOT NULL,"
                + KEY_RES_TIME + " TEXT NOT NULL,"
                + KEY_RES_GUESTS + " INTEGER NOT NULL,"
                + KEY_RES_STATUS + " TEXT NOT NULL"
                + ")";
        db.execSQL(CREATE_RES_TABLE);

        // Insert sample data
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add category column to existing table
            db.execSQL("ALTER TABLE " + TABLE_MENU + " ADD COLUMN " + KEY_MENU_CATEGORY + " TEXT DEFAULT 'Other'");
        }
    }

    //Insert sample menu items for testing
    private void insertSampleData(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        // Sample items
        values.put(KEY_MENU_NAME, "Margherita Pizza");
        values.put(KEY_MENU_PRICE, 12.99);
        values.put(KEY_MENU_IMAGE, "pizza");
        values.put(KEY_MENU_DESC, "Classic pizza with tomato and mozzarella");
        values.put(KEY_MENU_CATEGORY, "Mains");
        db.insert(TABLE_MENU, null, values);

        values.clear();
        values.put(KEY_MENU_NAME, "Caesar Salad");
        values.put(KEY_MENU_PRICE, 8.99);
        values.put(KEY_MENU_IMAGE, "salad");
        values.put(KEY_MENU_DESC, "Fresh romaine lettuce with Caesar dressing");
        values.put(KEY_MENU_CATEGORY, "Starters");
        db.insert(TABLE_MENU, null, values);

        values.clear();
        values.put(KEY_MENU_NAME, "Grilled Salmon");
        values.put(KEY_MENU_PRICE, 18.99);
        values.put(KEY_MENU_IMAGE, "salmon");
        values.put(KEY_MENU_DESC, "Fresh Atlantic salmon with vegetables");
        values.put(KEY_MENU_CATEGORY, "Mains");
        db.insert(TABLE_MENU, null, values);

        values.clear();
        values.put(KEY_MENU_NAME, "Beef Burger");
        values.put(KEY_MENU_PRICE, 10.99);
        values.put(KEY_MENU_IMAGE, "burger");
        values.put(KEY_MENU_DESC, "Juicy beef patty with lettuce and tomato");
        values.put(KEY_MENU_CATEGORY, "Mains");
        db.insert(TABLE_MENU, null, values);
    }

    //Menu operations:

    /**
     Add new menu item to database
     @param item MenuItem object to add
     @return row ID of inserted item, -1 if error
     */
    public long addMenuItem(MenuItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MENU_NAME, item.getName());
        values.put(KEY_MENU_PRICE, item.getPrice());
        values.put(KEY_MENU_IMAGE, item.getImageUrl());
        values.put(KEY_MENU_DESC, item.getDescription());
        values.put(KEY_MENU_CATEGORY, item.getCategory());

        long id = db.insert(TABLE_MENU, null, values);
        db.close();
        return id;
    }

    /**
     Get all menu items from database
     @return List of all MenuItem objects
     */
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> menuList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MENU;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MenuItem item = new MenuItem(
                        cursor.getInt(0),       // id
                        cursor.getString(1),    // name
                        cursor.getDouble(2),    // price
                        cursor.getString(3),    // image
                        cursor.getString(4),     // description
                        cursor.getString(5)     // category
                );
                menuList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return menuList;
    }



    /**
     Update existing menu item
     @param item MenuItem with updated information
     @return number of rows affected
     */
    public int updateMenuItem(MenuItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MENU_NAME, item.getName());
        values.put(KEY_MENU_PRICE, item.getPrice());
        values.put(KEY_MENU_IMAGE, item.getImageUrl());
        values.put(KEY_MENU_DESC, item.getDescription());
        values.put(KEY_MENU_CATEGORY, item.getCategory());

        int rowsAffected = db.update(TABLE_MENU, values, KEY_MENU_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
        return rowsAffected;
    }

    /**
     Delete menu item by ID
     @param id ID of menu item to delete
     */
    public void deleteMenuItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MENU, KEY_MENU_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // Reservation operations:

    /**
     Add new reservation to database
     @param reservation Reservation object to add
     @return row ID of inserted reservation, -1 if error
     */
    public long addReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_RES_GUEST, reservation.getGuestUsername());
        values.put(KEY_RES_DATE, reservation.getDate());
        values.put(KEY_RES_TIME, reservation.getTime());
        values.put(KEY_RES_GUESTS, reservation.getNumberOfGuests());
        values.put(KEY_RES_STATUS, reservation.getStatus());

        long id = db.insert(TABLE_RESERVATIONS, null, values);
        db.close();
        return id;
    }

    /**
     Get all reservations (for staff view)
     @return List of all Reservation objects
     */
    public List<Reservation> getAllReservations() {
        List<Reservation> reservationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RESERVATIONS +
                " ORDER BY " + KEY_RES_DATE + " DESC, " + KEY_RES_TIME + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Reservation reservation = new Reservation(
                        cursor.getInt(0),       // id
                        cursor.getString(1),    // guest_username
                        cursor.getString(2),    // date
                        cursor.getString(3),    // time
                        cursor.getInt(4),       // number_of_guests
                        cursor.getString(5)     // status
                );
                reservationList.add(reservation);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reservationList;
    }

    /**
     Get reservations for specific guest
     @param username Username of guest
     @return List of Reservation objects for this guest
     */
    public List<Reservation> getReservationsByGuest(String username) {
        List<Reservation> reservationList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RESERVATIONS,
                null,
                KEY_RES_GUEST + "=?",
                new String[]{username},
                null, null,
                KEY_RES_DATE + " DESC, " + KEY_RES_TIME + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Reservation reservation = new Reservation(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getString(5)
                );
                reservationList.add(reservation);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reservationList;
    }

    /**
     * Get menu items by category
     * @param category Category to filter by
     * @return List of MenuItem objects in that category
     */
    public List<MenuItem> getMenuItemsByCategory(String category) {
        List<MenuItem> menuList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MENU,
                null,
                KEY_MENU_CATEGORY + "=?",
                new String[]{category},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                MenuItem item = new MenuItem(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)
                );
                menuList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return menuList;
    }

    /**
     * Get all unique categories
     * @return List of category names
     */
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT " + KEY_MENU_CATEGORY + " FROM " + TABLE_MENU +
                        " WHERE " + KEY_MENU_CATEGORY + " IS NOT NULL ORDER BY " + KEY_MENU_CATEGORY,
                null);

        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return categories;
    }


    /**
     Update existing reservation
     @param reservation Reservation with updated information
     @return number of rows affected
     */
    public int updateReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_RES_DATE, reservation.getDate());
        values.put(KEY_RES_TIME, reservation.getTime());
        values.put(KEY_RES_GUESTS, reservation.getNumberOfGuests());
        values.put(KEY_RES_STATUS, reservation.getStatus());

        int rowsAffected = db.update(TABLE_RESERVATIONS, values, KEY_RES_ID + " = ?",
                new String[]{String.valueOf(reservation.getId())});
        db.close();
        return rowsAffected;
    }

    /**
     Delete reservation by ID
     @param id ID of reservation to delete
     */
    public void deleteReservation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RESERVATIONS, KEY_RES_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    /**
     Get reservation by ID
     @param id Reservation ID
     @return Reservation object or null if not found
     */
    public Reservation getReservationById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RESERVATIONS,
                null,
                KEY_RES_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        Reservation reservation = null;
        if (cursor.moveToFirst()) {
            reservation = new Reservation(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4),
                    cursor.getString(5)
            );
        }

        cursor.close();
        db.close();
        return reservation;
    }
}
