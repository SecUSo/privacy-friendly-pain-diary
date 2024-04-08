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
package org.secuso.privacyfriendlypaindiary.helpers

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.secuso.privacyfriendlypaindiary.R
import org.secuso.privacyfriendlypaindiary.database.PainDiaryDatabaseService.Companion.getInstance
import java.util.Calendar

/**
 * Responsible for scheduling and displaying snoozable daily reminder notifications
 * at the reminder time set by the user (see also [org.secuso.privacyfriendlypaindiary.activities.SettingsActivity]).
 *
 * @author Susanne Felsen
 * @version 20180130
 *
 * Inspiration from: [Link 1](https://medium.com/google-developers/scheduling-jobs-like-a-pro-with-jobscheduler-286ef8510129)
 * [Link 2](https://blog.klinkerapps.com/android-o-background-services/).
 */
class NotificationJobService : JobService() {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    //for background work start an AsyncTask, Thread or IntentService -> return true
    override fun onStartJob(params: JobParameters): Boolean {
        val notificationJobService = this
        scope.launch {
            val service = getInstance(notificationJobService)
            val entry = service.getDiaryEntryByDate(Calendar.getInstance().time)
            if (entry == null) { //no entry has been made yet
                val notificationHelper = NotificationHelper(notificationJobService)
                val notificationBuilder = notificationHelper.getNotificationBuilder(
                    getString(R.string.notification_title),
                    getString(R.string.notification_text)
                )
                val intent = Intent(notificationJobService, NotificationReceiver::class.java)
                intent.putExtra(NOTIFICATION_ID, NOTIFICATION_ID_VALUE)
                intent.action = ACTION_SNOOZE
                val pendingIntent =
                    PendingIntent.getBroadcast(
                        notificationJobService,
                        1,
                        intent,
                        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                    )
                val action: Notification.Action
                action = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Notification.Action.Builder(
                        Icon.createWithResource(
                            notificationJobService,
                            R.drawable.ic_snooze
                        ), getString(R.string.snooze) + " (1h)", pendingIntent
                    ).build()
                } else {
                    Notification.Action.Builder(
                        R.drawable.ic_snooze,
                        getString(R.string.snooze) + " (1h)",
                        pendingIntent
                    ).build()
                }
                notificationBuilder.addAction(action).build()
                notificationHelper.notify(NOTIFICATION_ID_VALUE, notificationBuilder)
            }
            jobFinished(params, false)
            if (params.jobId == PERIODIC_JOB_ID) {
                scheduleJob(notificationJobService)
            }
        }
        return false
    }

    override fun onStopJob(params: JobParameters): Boolean {
        scope.cancel()
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_SNOOZE) {
                val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val notificationID = intent.getIntExtra(NOTIFICATION_ID, 0)
                manager.cancel(notificationID)

                //notification is snoozed for one hour
                scheduleSnoozedJob(context, ONE_HOUR)
            }
        }
    }

    companion object {
        private val TAG = NotificationJobService::class.java.simpleName
        private const val KEY_PREF_REMINDER_TIME = "pref_reminder_time"
        private const val ACTION_SNOOZE = "org.secuso.privacyfriendlypaindiary.action.SNOOZE"
        private const val PERIODIC_JOB_ID = 42
        private const val SNOOZED_JOB_ID = 13
        private const val NOTIFICATION_ID = "notification_id"
        private const val NOTIFICATION_ID_VALUE = 42

        //in milliseconds
        private const val ONE_HOUR = (60 * 60 * 1000).toLong()
        private const val FLEX_INTERVAL = (30 * 1000 //30 seconds
                ).toLong()

        fun scheduleJob(context: Context) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val alertTimeInMillis = sharedPreferences.getLong(KEY_PREF_REMINDER_TIME, 64800000)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = alertTimeInMillis
            calendar[Calendar.getInstance()[Calendar.YEAR], Calendar.getInstance()[Calendar.MONTH]] =
                Calendar.getInstance()[Calendar.DAY_OF_MONTH]
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            val timeToAlertInMillis = calendar.timeInMillis - Calendar.getInstance().timeInMillis
            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(
                JobInfo.Builder(
                    PERIODIC_JOB_ID,
                    ComponentName(context, NotificationJobService::class.java)
                )
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                    .setMinimumLatency(timeToAlertInMillis)
                    .setOverrideDeadline(timeToAlertInMillis + FLEX_INTERVAL)
                    .setRequiresDeviceIdle(false)
                    .setRequiresCharging(false)
                    .setPersisted(true)
                    .build()
            )
        }

        protected fun scheduleSnoozedJob(context: Context, millisToWait: Long) {
            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(
                JobInfo.Builder(
                    SNOOZED_JOB_ID,
                    ComponentName(context, NotificationJobService::class.java)
                )
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                    .setMinimumLatency(millisToWait)
                    .setOverrideDeadline(millisToWait + FLEX_INTERVAL)
                    .setRequiresDeviceIdle(false)
                    .setRequiresCharging(false)
                    .setPersisted(true)
                    .build()
            )
        }

        fun cancelJob(context: Context) {
            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.cancel(PERIODIC_JOB_ID)
        }
    }
}