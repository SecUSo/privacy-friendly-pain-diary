package org.secuso.privacyfriendlypaindiary.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import org.secuso.privacyfriendlypaindiary.R;

/**
 * Inspiration from: <a href="https://developer.android.com/guide/topics/ui/notifiers/notifications.html"/> and <a href="https://www.androidauthority.com/android-8-0-oreo-app-implementing-notification-channels-801097/"/>.
 *
 * @author Susanne Felsen
 * @version 20180110
 */
public class NotificationHelper extends ContextWrapper {

    private static final String TAG = NotificationHelper.class.getSimpleName();

    private static final String CHANNEL_ID = "reminders";

    private NotificationManager manager;

    public NotificationHelper(Context context) {
        super(context);
        createChannel();
    }

    public void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    getString(R.string.reminders_channel_title), NotificationManager.IMPORTANCE_DEFAULT);
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.RED);
//            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(notificationChannel);
        }
    }

    public Notification.Builder getNotification(String title, String body) {
        Notification.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(getApplicationContext(), CHANNEL_ID);
        } else {
            notificationBuilder = new Notification.Builder(getApplicationContext());
        }
        return notificationBuilder
                .setSmallIcon(R.drawable.splash_screen) //TODO: change icon
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);
    }

    public void notify(int id, Notification.Builder notification) {
        getManager().notify(id, notification.build());
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

}
