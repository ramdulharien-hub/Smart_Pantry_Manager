package com.example.smartpantrymanager;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExpiryNotificationHelper {

    public static final String CHANNEL_ID =
            "expiry_alerts";

    private static final String CHANNEL_NAME =
            "Expiry Alerts";

    private static final String CHANNEL_DESCRIPTION =
            "Notifications for pantry items that are expiring soon";

    private static final String ACTION_EXPIRY_ALERT =
            "com.example.smartpantrymanager.EXPIRY_ALERT";

    private static final String EXTRA_ITEM_ID =
            "expiry_item_id";

    private static final int ALERT_HOUR = 9;

    private ExpiryNotificationHelper() {
        // Prevent creating an instance
    }

    public static void createNotificationChannel(
            Context context
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_DEFAULT
                    );

            channel.setDescription(
                    CHANNEL_DESCRIPTION
            );

            NotificationManager notificationManager =
                    context.getSystemService(
                            NotificationManager.class
                    );

            if (notificationManager != null) {

                notificationManager.createNotificationChannel(
                        channel
                );
            }
        }
    }

    public static void scheduleExpiryAlert(
            Context context,
            PantryItem item
    ) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        "SmartPantrySettings",
                        Context.MODE_PRIVATE
                );

        boolean alertsEnabled =
                preferences.getBoolean(
                        "expiry_alerts",
                        true
                );

        if (!alertsEnabled) {
            return;
        }

        String expiryDateText =
                item.getExpiryDate();

        if (expiryDateText == null ||
                expiryDateText.trim().isEmpty()) {

            return;
        }

        Date expiryDate;

        try {

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    );

            dateFormat.setLenient(false);

            expiryDate =
                    dateFormat.parse(
                            expiryDateText.trim()
                    );

        } catch (ParseException e) {

            return;
        }

        if (expiryDate == null) {
            return;
        }

        Calendar expiryCalendar =
                Calendar.getInstance();

        expiryCalendar.setTime(
                expiryDate
        );

        expiryCalendar.set(
                Calendar.HOUR_OF_DAY,
                ALERT_HOUR
        );

        expiryCalendar.set(
                Calendar.MINUTE,
                0
        );

        expiryCalendar.set(
                Calendar.SECOND,
                0
        );

        expiryCalendar.set(
                Calendar.MILLISECOND,
                0
        );

        Calendar alertCalendar =
                (Calendar) expiryCalendar.clone();

        // Alert one day before expiry
        alertCalendar.add(
                Calendar.DAY_OF_YEAR,
                -1
        );

        Calendar now =
                Calendar.getInstance();

        // If the alert time has already passed but
        // the item expires today or tomorrow,
        // schedule the notification immediately.
        if (alertCalendar.before(now)) {

            Calendar today =
                    Calendar.getInstance();

            today.set(
                    Calendar.HOUR_OF_DAY,
                    0
            );

            today.set(
                    Calendar.MINUTE,
                    0
            );

            today.set(
                    Calendar.SECOND,
                    0
            );

            today.set(
                    Calendar.MILLISECOND,
                    0
            );

            Calendar tomorrow =
                    (Calendar) today.clone();

            tomorrow.add(
                    Calendar.DAY_OF_YEAR,
                    1
            );

            Calendar expiryDay =
                    (Calendar) expiryCalendar.clone();

            expiryDay.set(
                    Calendar.HOUR_OF_DAY,
                    0
            );

            expiryDay.set(
                    Calendar.MINUTE,
                    0
            );

            expiryDay.set(
                    Calendar.SECOND,
                    0
            );

            expiryDay.set(
                    Calendar.MILLISECOND,
                    0
            );

            if (expiryDay.equals(today) ||
                    expiryDay.equals(tomorrow)) {

                alertCalendar =
                        Calendar.getInstance();

            } else {

                // Expiry date is already in the past.
                return;
            }
        }

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(
                        Context.ALARM_SERVICE
                );

        if (alarmManager == null) {
            return;
        }

        Intent intent =
                new Intent(
                        context,
                        ExpiryNotificationReceiver.class
                );

        intent.setAction(
                ACTION_EXPIRY_ALERT
        );

        intent.putExtra(
                EXTRA_ITEM_ID,
                item.getId()
        );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        item.getId(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alertCalendar.getTimeInMillis(),
                pendingIntent
        );
    }

    public static void cancelExpiryAlert(
            Context context,
            int itemId
    ) {

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(
                        Context.ALARM_SERVICE
                );

        if (alarmManager == null) {
            return;
        }

        Intent intent =
                new Intent(
                        context,
                        ExpiryNotificationReceiver.class
                );

        intent.setAction(
                ACTION_EXPIRY_ALERT
        );

        intent.putExtra(
                EXTRA_ITEM_ID,
                itemId
        );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        itemId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        alarmManager.cancel(
                pendingIntent
        );

        pendingIntent.cancel();
    }

    public static void cancelAllExpiryAlerts(
            Context context
    ) {

        DatabaseHelper databaseHelper =
                new DatabaseHelper(context);

        ArrayList<PantryItem> pantryItems =
                databaseHelper.getAllPantryItems();

        for (PantryItem item : pantryItems) {

            cancelExpiryAlert(
                    context,
                    item.getId()
            );
        }
    }

    public static void scheduleAllExpiryAlerts(
            Context context
    ) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        "SmartPantrySettings",
                        Context.MODE_PRIVATE
                );

        boolean alertsEnabled =
                preferences.getBoolean(
                        "expiry_alerts",
                        true
                );

        if (!alertsEnabled) {
            return;
        }

        createNotificationChannel(context);

        DatabaseHelper databaseHelper =
                new DatabaseHelper(context);

        ArrayList<PantryItem> pantryItems =
                databaseHelper.getAllPantryItems();

        for (PantryItem item : pantryItems) {

            scheduleExpiryAlert(
                    context,
                    item
            );
        }
    }

    public static void showExpiryNotification(
            Context context,
            PantryItem item
    ) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        "SmartPantrySettings",
                        Context.MODE_PRIVATE
                );

        boolean alertsEnabled =
                preferences.getBoolean(
                        "expiry_alerts",
                        true
                );

        if (!alertsEnabled) {
            return;
        }

        createNotificationChannel(context);

        NotificationManager notificationManager =
                (NotificationManager)
                        context.getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );

        if (notificationManager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        String expiryDate =
                item.getExpiryDate();

        String message =
                item.getName()
                        + " expires on "
                        + expiryDate
                        + ".";

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        CHANNEL_ID
                )
                        .setSmallIcon(
                                android.R.drawable.ic_dialog_info
                        )
                        .setContentTitle(
                                "Pantry Expiry Alert"
                        )
                        .setContentText(
                                message
                        )
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(message)
                        )
                        .setPriority(
                                NotificationCompat.PRIORITY_DEFAULT
                        )
                        .setAutoCancel(true);

        notificationManager.notify(
                item.getId(),
                builder.build()
        );
    }
}