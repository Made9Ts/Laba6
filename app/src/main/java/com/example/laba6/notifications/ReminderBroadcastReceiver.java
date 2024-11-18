package com.example.laba6.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        long reminderId = intent.getLongExtra("id", -1);

        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.sendNotification(title, text, reminderId);
    }
}