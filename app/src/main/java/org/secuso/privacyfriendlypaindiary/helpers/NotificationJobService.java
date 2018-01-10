package org.secuso.privacyfriendlypaindiary.helpers;

import android.app.Notification;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;

import org.secuso.privacyfriendlypaindiary.R;

/**
 * Inspiration from: <a href="https://medium.com/google-developers/scheduling-jobs-like-a-pro-with-jobscheduler-286ef8510129"/> and <a href="https://blog.klinkerapps.com/android-o-background-services/"/>.
 *
 * @author Susanne Felsen
 * @version 20180110
 */
public class NotificationJobService extends JobService {

    private static final String TAG = NotificationJobService.class.getSimpleName();

    private static final int JOB_ID = 42;
    private static final int NOTIFICATION_ID = 42;

    private static final int ONE_DAY = 24 * 60 * 60 * 1000; //in milliseconds

    //for background work start an AsyncTask, Thread or IntentService -> return true
    @Override
    public boolean onStartJob(JobParameters params) {
        NotificationHelper notificationHelper = new NotificationHelper(this);
        Notification.Builder notificationBuilder = notificationHelper.getNotification(getString(R.string.notification_title), getString(R.string.notification_text));
        notificationHelper.notify(NOTIFICATION_ID, notificationBuilder);

        jobFinished(params, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }


    public static void scheduleJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(JOB_ID, new ComponentName(context, NotificationJobService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setPeriodic(ONE_DAY)
                .setRequiresDeviceIdle(false)
                .setRequiresCharging(false)
                .setPersisted(true)
                .build());
    }

    public static void cancelJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(JOB_ID);
    }

}
