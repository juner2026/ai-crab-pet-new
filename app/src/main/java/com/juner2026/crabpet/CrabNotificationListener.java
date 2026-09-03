package com.juner2026.crabpet;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class CrabNotificationListener extends NotificationListenerService {
    public static volatile String latest = "";
    @Override public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null || sbn.getNotification() == null) return;
        CharSequence text = sbn.getNotification().extras.getCharSequence("android.text");
        if (text != null) latest = text.toString();
    }
}
