/*
    Privacy Friendly Pain Diary is licensed under the GPLv3.
    Copyright (C) 2018  Susanne Felsen, Rybien Sinjari

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.secuso.privacyfriendlypaindiary.helpers;

import android.app.Notification;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.DBServiceInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;

import java.util.Calendar;

/**
 * Inspiration from: <a href="https://medium.com/google-developers/scheduling-jobs-like-a-pro-with-jobscheduler-286ef8510129"/> and <a href="https://blog.klinkerapps.com/android-o-background-services/"/>.
 *
 * @author Susanne Felsen
 * @version 20180130
 */
public class NotificationJobService extends JobService {

    private static final String TAG = NotificationJobService.class.getSimpleName();
    private static final String KEY_PREF_REMINDER_TIME = "pref_reminder_time";

    private static final int PERIODIC_JOB_ID = 42;
    private static final int SNOOZED_JOB_ID = 13;

    private static final int NOTIFICATION_ID = 42;

    private static final long ONE_DAY = 24 * 60 * 60 * 1000; //in milliseconds
    private static final long FLEX_INTERVAL = 60 * 1000; //1 minute

    //for background work start an AsyncTask, Thread or IntentService -> return true
    @Override
    public boolean onStartJob(JobParameters params) {
        DBServiceInterface service = DBService.getInstance(this);
        DiaryEntryInterface entry = service.getDiaryEntryByDate(Calendar.getInstance().getTime());
        if(entry == null) { //no entry has been made yet
            NotificationHelper notificationHelper = new NotificationHelper(this);
            Notification.Builder notificationBuilder = notificationHelper.getNotification(getString(R.string.notification_title), getString(R.string.notification_text));
            notificationHelper.notify(NOTIFICATION_ID, notificationBuilder);
        }
        jobFinished(params, false);

        scheduleJob(this);

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

    public static void cancelJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(PERIODIC_JOB_ID);
    }

}
