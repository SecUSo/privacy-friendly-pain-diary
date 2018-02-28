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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.DBServiceInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;

import java.util.Calendar;

/**
 * Responsible for scheduling and displaying snoozable daily reminder notifications
 * at the reminder time set by the user (see also {@link org.secuso.privacyfriendlypaindiary.activities.SettingsActivity}).
 *
 * @author Susanne Felsen
 * @version 20180130
 *
 * Inspiration from: <a href="https://medium.com/google-developers/scheduling-jobs-like-a-pro-with-jobscheduler-286ef8510129">Link 1</a>
 * <a href="https://blog.klinkerapps.com/android-o-background-services/">Link 2</a>.
 */
public class NotificationJobService extends JobService {

    private static final String TAG = NotificationJobService.class.getSimpleName();
    private static final String KEY_PREF_REMINDER_TIME = "pref_reminder_time";
    private static final String ACTION_SNOOZE = "org.secuso.privacyfriendlypaindiary.action.SNOOZE";

    private static final int PERIODIC_JOB_ID = 42;
    private static final int SNOOZED_JOB_ID = 13;

    private static final String NOTIFICATION_ID = "notification_id";
    private static final int NOTIFICATION_ID_VALUE = 42;

    //in milliseconds
    private static final long ONE_HOUR = 60 * 60 * 1000;
    private static final long FLEX_INTERVAL = 30 * 1000; //30 seconds

    //for background work start an AsyncTask, Thread or IntentService -> return true
    @Override
    public boolean onStartJob(JobParameters params) {
        DBServiceInterface service = DBService.getInstance(this);
        DiaryEntryInterface entry = service.getDiaryEntryByDate(Calendar.getInstance().getTime());
        if(entry == null) { //no entry has been made yet
            NotificationHelper notificationHelper = new NotificationHelper(this);
            Notification.Builder notificationBuilder = notificationHelper.getNotificationBuilder(getString(R.string.notification_title), getString(R.string.notification_text));

            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra(NOTIFICATION_ID, NOTIFICATION_ID_VALUE);
            intent.setAction(ACTION_SNOOZE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
            Notification.Action action;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                action = new Notification.Action.Builder(Icon.createWithResource(this, R.drawable.ic_snooze), getString(R.string.snooze) + " (1h)", pendingIntent).build();
            } else {
                action = new Notification.Action.Builder(R.drawable.ic_snooze, getString(R.string.snooze) + " (1h)", pendingIntent).build();
            }
            notificationBuilder.addAction(action).build();
            notificationHelper.notify(NOTIFICATION_ID_VALUE, notificationBuilder);
        }
        jobFinished(params, false);

        if(params.getJobId() == PERIODIC_JOB_ID) {
            scheduleJob(this);
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public static void scheduleJob(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long alertTimeInMillis = sharedPreferences.getLong(KEY_PREF_REMINDER_TIME, 64800000);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(alertTimeInMillis);
        calendar.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        long timeToAlertInMillis = calendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(PERIODIC_JOB_ID, new ComponentName(context, NotificationJobService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setMinimumLatency(timeToAlertInMillis)
                .setOverrideDeadline(timeToAlertInMillis + FLEX_INTERVAL)
                .setRequiresDeviceIdle(false)
                .setRequiresCharging(false)
                .setPersisted(true)
                .build());
    }

    protected static void scheduleSnoozedJob(Context context, long millisToWait) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(SNOOZED_JOB_ID, new ComponentName(context, NotificationJobService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setMinimumLatency(millisToWait)
                .setOverrideDeadline(millisToWait + FLEX_INTERVAL)
                .setRequiresDeviceIdle(false)
                .setRequiresCharging(false)
                .setPersisted(true)
                .build());
    }

    public static void cancelJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(PERIODIC_JOB_ID);
    }

    public static class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_SNOOZE)) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                int notificationID = intent.getIntExtra(NOTIFICATION_ID, 0);
                manager.cancel(notificationID);

                //notification is snoozed for one hour
                NotificationJobService.scheduleSnoozedJob(context, ONE_HOUR);
            }
        }
    }

}
