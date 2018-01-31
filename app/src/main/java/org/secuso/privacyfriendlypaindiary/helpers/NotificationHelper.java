/*
    This file is part of Privacy Friendly Pain Diary.

    Privacy Friendly Pain Diary is free software: you can redistribute it
    and/or modify it under the terms of the GNU General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.secuso.privacyfriendlypaindiary.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.activities.MainActivity;

/**
 * Inspiration from: <a href="https://developer.android.com/guide/topics/ui/notifiers/notifications.html"/> and <a href="https://www.androidauthority.com/android-8-0-oreo-app-implementing-notification-channels-801097/"/>.
 *
 * @author Susanne Felsen
 * @version 20180130
 */
public class NotificationHelper extends ContextWrapper {

    private static final String TAG = NotificationHelper.class.getSimpleName();
    private static final int COLOR_LIGHTBLUE = Color.parseColor("#0274b2");

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

    //<a href="https://materialdoc.com/patterns/notifications/"/>
    public Notification.Builder getNotificationBuilder(String title, String body) {
        Notification.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(getApplicationContext(), CHANNEL_ID);
        } else {
            notificationBuilder = new Notification.Builder(getApplicationContext());
        }

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
//        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return notificationBuilder
                .setSmallIcon(R.drawable.ic_local_hospital)
                .setColor(COLOR_LIGHTBLUE)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new Notification.BigTextStyle())
                .setContentIntent(resultPendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
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
