package org.secuso.privacyfriendlypaindiary


import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import androidx.work.Configuration
import org.secuso.privacyfriendlybackup.api.pfa.BackupManager
import org.secuso.privacyfriendlypaindiary.backup.BackupCreator
import org.secuso.privacyfriendlypaindiary.backup.BackupRestorer
import java.util.Calendar


class PFAPainDiaryApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        migrateReminderTimeToMillisOfDay()
        BackupManager.backupCreator = BackupCreator()
        BackupManager.backupRestorer = BackupRestorer()
    }

    /**
     * The reminder time used to be stored as a full epoch timestamp (a Long). Only the
     * hour and minute of that value were ever used, so it is now stored as milliseconds
     * since midnight (an Int). Convert any value left over from an older version once.
     */
    private fun migrateReminderTimeToMillisOfDay() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val storedValue = preferences.all[KEY_PREF_REMINDER_TIME]
        if (storedValue is Long) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = storedValue
            val millisOfDay = calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000 +
                    calendar.get(Calendar.MINUTE) * 60 * 1000
            preferences.edit()
                .remove(KEY_PREF_REMINDER_TIME)
                .putInt(KEY_PREF_REMINDER_TIME, millisOfDay)
                .apply()
        }
    }

    override val workManagerConfiguration = Configuration.Builder().setMinimumLoggingLevel(Log.INFO).build()

    companion object {
        private const val KEY_PREF_REMINDER_TIME = "pref_reminder_time"
    }
}