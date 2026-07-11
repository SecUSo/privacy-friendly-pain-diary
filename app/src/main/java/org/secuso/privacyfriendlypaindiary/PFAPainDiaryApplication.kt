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


/**
 * The application class from the pain diary. It extend the PFA-Core PFApplication and give to the
 * library the app data, the main activity and the database backup config. It run also two small
 * migrations for the old users, so the reminder time and the user id are still working after the
 * update.
 */
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
        migrateUserIdToDefaultPrefs()
        super.onCreate()
        // Build the application data eagerly on the main thread. PFApplicationData wires up
        // LiveData transformations (e.g. the theme), and LiveData.setValue may only run on the
        // main thread. The backup runs on a background thread, so building it lazily there would
        // crash; building it here makes the singleton ready before the backup worker uses it.
        PFApplicationData.instance(this)
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

    /**
     * The user id used to live in its own "privacy_friendly_apps" preferences file, which the
     * PFA-Core backup does not cover. It is now kept in the default preferences (and backed up).
     * Move any value left over from an older version across once so an upgrade keeps the user link.
     */
    private fun migrateUserIdToDefaultPrefs() {
        val defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (defaultPrefs.contains(KEY_USER_ID)) return
        val legacyUserId = getSharedPreferences(LEGACY_PREF_FILE, MODE_PRIVATE).getLong(KEY_USER_ID, 0L)
        if (legacyUserId > 0L) {
            defaultPrefs.edit().putInt(KEY_USER_ID, legacyUserId.toInt()).apply()
        }
    }

    override val workManagerConfiguration = Configuration.Builder().setMinimumLoggingLevel(Log.INFO).build()

    companion object {
        private const val KEY_PREF_REMINDER_TIME = "pref_reminder_time"
        private const val KEY_USER_ID = "userID"
        private const val LEGACY_PREF_FILE = "privacy_friendly_apps"
    }
}
