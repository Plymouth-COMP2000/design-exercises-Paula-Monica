package com.example.restaurantmanager.activities.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.activities.guest.GuestMakeReservationActivity;
import com.example.restaurantmanager.activities.staff.StaffManageReservationsActivity;
import com.example.restaurantmanager.models.NotificationPreferences;
import com.example.restaurantmanager.models.Reservation;

/**
 NotificationHelper - Manages all app notifications
 Design Pattern: Singleton
 SOLID: Single Responsibility - Only handles notification operations
 Threading: Can be called from any thread, posts to main thread internally
 */
public class NotificationHelper {

    private static final String TAG = "NotificationHelper";

    // Notification channel IDs
    private static final String CHANNEL_ID_GUEST = "guest_notifications";
    private static final String CHANNEL_ID_STAFF = "staff_notifications";

    // Notification IDs
    private static final int NOTIF_ID_GUEST_CANCELLATION = 1001;
    private static final int NOTIF_ID_GUEST_DAY_BEFORE = 1002;
    private static final int NOTIF_ID_GUEST_HOUR_BEFORE = 1003;
    private static final int NOTIF_ID_STAFF_NEW_RESERVATION = 2001;
    private static final int NOTIF_ID_STAFF_RESERVATION_CHANGE = 2002;
    private static final int NOTIF_ID_STAFF_15_MIN_BEFORE = 2003;
    private static final int NOTIF_ID_STAFF_30_MIN_BEFORE = 2004;

    private static NotificationHelper instance;
    private Context context;
    private NotificationManager notificationManager;
    private NotificationPreferences preferences;

    //Private constructor (Singleton pattern)
    private NotificationHelper(Context context) {
        this.context = context.getApplicationContext();
        this.notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.preferences = NotificationPreferences.getInstance(context);
        createNotificationChannels();
    }

    //Get singleton instance
    public static synchronized NotificationHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationHelper(context);
        }
        return instance;
    }

    //Create notification channels
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Guest notification channel
            NotificationChannel guestChannel = new NotificationChannel(
                    CHANNEL_ID_GUEST,
                    "Guest Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            guestChannel.setDescription("Notifications for restaurant guests");
            guestChannel.enableLights(true);
            guestChannel.setLightColor(Color.BLUE);
            guestChannel.enableVibration(true);
            notificationManager.createNotificationChannel(guestChannel);

            // Staff notification channel
            NotificationChannel staffChannel = new NotificationChannel(
                    CHANNEL_ID_STAFF,
                    "Staff Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            staffChannel.setDescription("Notifications for restaurant staff");
            staffChannel.enableLights(true);
            staffChannel.setLightColor(Color.GREEN);
            staffChannel.enableVibration(true);
            notificationManager.createNotificationChannel(staffChannel);

            Log.d(TAG, "Notification channels created");
        }
    }

    // GUEST NOTIFICATIONS

    /**
     Send notification when staff cancels guest's reservation
     @param reservation The cancelled reservation
     */
    public void sendGuestReservationCancelled(Reservation reservation) {
        // Check if user has this notification enabled
        if (!preferences.isGuestCancellationEnabled()) {
            Log.d(TAG, "Guest cancellation notification disabled by user");
            return;
        }

        String title = "Reservation Cancelled";
        String message = "Your reservation for " + reservation.getDate() +
                " at " + reservation.getTime() + " has been cancelled by the restaurant.";

        // Create intent to open reservations screen
        Intent intent = new Intent(context, GuestMakeReservationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                NOTIF_ID_GUEST_CANCELLATION,
                intent,
                PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_GUEST)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Show notification
        notificationManager.notify(NOTIF_ID_GUEST_CANCELLATION, builder.build());
        Log.d(TAG, "Guest cancellation notification sent");
    }

    /**
     Send day before reminder to guest
     @param reservation The reservation
     */
    public void sendGuestDayBeforeReminder(Reservation reservation) {
        if (!preferences.isGuestDayBeforeEnabled()) {
            Log.d(TAG, "Day before reminder disabled by user");
            return;
        }

        String title = "Reservation Tomorrow";
        String message = "Reminder: You have a reservation tomorrow at " +
                reservation.getTime() + " for " + reservation.getNumberOfGuests() + " guests.";

        sendGuestNotification(
                NOTIF_ID_GUEST_DAY_BEFORE,
                title,
                message,
                GuestMakeReservationActivity.class
        );
    }

    /**
     Send hour before reminder to guest
     @param reservation The reservation
     */
    public void sendGuestHourBeforeReminder(Reservation reservation) {
        if (!preferences.isGuestHourBeforeEnabled()) {
            Log.d(TAG, "Hour before reminder disabled by user");
            return;
        }

        String title = "Reservation in 1 Hour";
        String message = "Your reservation is in 1 hour at " + reservation.getTime() +
                ". See you soon!";

        sendGuestNotification(
                NOTIF_ID_GUEST_HOUR_BEFORE,
                title,
                message,
                GuestMakeReservationActivity.class
        );
    }

    //Helper method to send guest notifications
    private void sendGuestNotification(int notificationId, String title, String message, Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_GUEST)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notificationId, builder.build());
        Log.d(TAG, "Guest notification sent: " + title);
    }

    //STAFF NOTIFICATIONS

    /**
     Send notification to staff when guest creates new reservation
     @param reservation The new reservation
     */
    public void sendStaffNewReservation(Reservation reservation) {
        if (!preferences.isStaffNewReservationEnabled()) {
            Log.d(TAG, "New reservation notification disabled by staff");
            return;
        }

        String title = "New Reservation";
        String message = reservation.getGuestUsername() + " made a reservation for " +
                reservation.getDate() + " at " + reservation.getTime() +
                " (" + reservation.getNumberOfGuests() + " guests)";

        sendStaffNotification(
                NOTIF_ID_STAFF_NEW_RESERVATION,
                title,
                message,
                StaffManageReservationsActivity.class
        );
    }

    /**
     Send notification to staff when guest changes reservation
     @param reservation The changed reservation
     */
    public void sendStaffReservationChanged(Reservation reservation) {
        if (!preferences.isStaffReservationChangesEnabled()) {
            Log.d(TAG, "Reservation change notification disabled by staff");
            return;
        }

        String title = "Reservation Changed";
        String message = reservation.getGuestUsername() + " updated their reservation to " +
                reservation.getDate() + " at " + reservation.getTime() +
                " (" + reservation.getNumberOfGuests() + " guests)";

        sendStaffNotification(
                NOTIF_ID_STAFF_RESERVATION_CHANGE,
                title,
                message,
                StaffManageReservationsActivity.class
        );
    }

    /**
     Send 30 minute before reminder to staff
     @param reservation The reservation
     */
    public void sendStaff30MinBeforeReminder(Reservation reservation) {
        if (!preferences.isStaff30MinBeforeEnabled()) {
            Log.d(TAG, "30 min reminder disabled by staff");
            return;
        }

        String title = "Reservation in 30 Minutes";
        String message = reservation.getGuestUsername() + " arriving at " +
                reservation.getTime() + " (" + reservation.getNumberOfGuests() + " guests)";

        sendStaffNotification(
                NOTIF_ID_STAFF_30_MIN_BEFORE,
                title,
                message,
                StaffManageReservationsActivity.class
        );
    }

    /**
     Send 15 minute before reminder to staff
     @param reservation The reservation
     */
    public void sendStaff15MinBeforeReminder(Reservation reservation) {
        if (!preferences.isStaff15MinBeforeEnabled()) {
            Log.d(TAG, "15 min reminder disabled by staff");
            return;
        }

        String title = "Reservation in 15 Minutes";
        String message = reservation.getGuestUsername() + " arriving soon at " +
                reservation.getTime() + " (" + reservation.getNumberOfGuests() + " guests)";

        sendStaffNotification(
                NOTIF_ID_STAFF_15_MIN_BEFORE,
                title,
                message,
                StaffManageReservationsActivity.class
        );
    }

    //Helper method to send staff notifications
    private void sendStaffNotification(int notificationId, String title, String message, Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_STAFF)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notificationId, builder.build());
        Log.d(TAG, "Staff notification sent: " + title);
    }

    //Cancel all notifications (for logout)
    public void cancelAllNotifications() {
        notificationManager.cancelAll();
        Log.d(TAG, "All notifications cancelled");
    }
}