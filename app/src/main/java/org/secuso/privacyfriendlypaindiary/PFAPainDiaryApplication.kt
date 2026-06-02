package org.secuso.privacyfriendlypaindiary


import android.app.Activity
import android.preference.PreferenceManager
import android.util.JsonWriter
import android.util.Log
import androidx.work.Configuration
import org.secuso.pfacore.application.BackupDatabaseConfig
import org.secuso.pfacore.application.PFAppBackup
import org.secuso.pfacore.application.RoomDatabaseConfig
import org.secuso.pfacore.ui.PFApplication
import org.secuso.pfacore.ui.PFData
import org.secuso.privacyfriendlypaindiary.activities.MainActivity
import org.secuso.privacyfriendlypaindiary.database.PainDiaryDatabase
import java.util.Calendar


class PFAPainDiaryApplication : PFApplication() {

    override val name: String
        get() = getString(R.string.app_name)

    override val data: PFData
        get() = PFApplicationData.instance(this).data

    override val mainActivity: Class<out Activity> = MainActivity::class.java

    override val database: BackupDatabaseConfig
        get() = RoomDatabaseConfig(this, PainDiaryDatabase.DATABASE_NAME, PainDiaryDatabase::class.java)

    // The PFA-Core BackupCreator only writes the registered appBackup managers and the
    // preferences; it does not call database.backup() itself. We therefore back up the
    // database through an appBackup entry. On restore the library routes the "database"
    // key to the database config above.
    override val appBackup: List<PFAppBackup>
        get() = listOf(object : PFAppBackup {
            override val key = "database"
            override fun backup(writer: JsonWriter): JsonWriter {
                database.backup(writer)
                return writer
            }
        })

    override fun onCreate() {
        migrateReminderTimeToMillisOfDay()
        super.onCreate()
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
