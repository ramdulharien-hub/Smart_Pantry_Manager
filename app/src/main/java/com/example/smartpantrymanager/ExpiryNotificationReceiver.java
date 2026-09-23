package com.example.smartpantrymanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class ExpiryNotificationReceiver
        extends BroadcastReceiver {

    @Override
    public void onReceive(
            Context context,
            Intent intent
    ) {

        int itemId =
                intent.getIntExtra(
                        "expiry_item_id",
                        -1
                );

        if (itemId == -1) {
            return;
        }

        DatabaseHelper databaseHelper =
                new DatabaseHelper(context);

        ArrayList<PantryItem> pantryItems =
                databaseHelper.getAllPantryItems();

        for (PantryItem item : pantryItems) {

            if (item.getId() == itemId) {

                ExpiryNotificationHelper
                        .showExpiryNotification(
                                context,
                                item
                        );

                break;
            }
        }
    }
}