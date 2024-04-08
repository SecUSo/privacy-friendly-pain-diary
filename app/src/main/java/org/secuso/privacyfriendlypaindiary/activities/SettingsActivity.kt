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
package org.secuso.privacyfriendlypaindiary.activities

import android.Manifest
import android.app.AlertDialog
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.secuso.privacyfriendlypaindiary.R
import org.secuso.privacyfriendlypaindiary.database.PainDiaryDatabaseService
import org.secuso.privacyfriendlypaindiary.database.entities.impl.AbstractPersistentObject
import org.secuso.privacyfriendlypaindiary.helpers.NotificationJobService
import org.secuso.privacyfriendlypaindiary.tutorial.PrefManager

/**
 * This activity provides methods to enable or disable certain features such as
 * daily reminders and whether the medication last entered is automatically added
 * to new entries based on preferences set by the user. It also provides methods
 * to set the reminder time or reset the app.
 *
 * @author Susanne Felsen
 * @version 20180228
 *
 * Inspiration from: [Link 1](https://developer.android.com/guide/topics/ui/settings.html)
 * [Link 2](https://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-su/4325239#4325239)
 */
class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        overridePendingTransition(0, 0)
    }

    override fun getNavigationDrawerID(): Int {
        return R.id.nav_settings
    }

    class GeneralPreferenceFragment : PreferenceFragment(), OnSharedPreferenceChangeListener {
        companion object {
            const val REQUEST_CODE_POST_NOTIFICATION = 3
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            initPreferences()
        }

        private fun initPreferences() {
            addPreferencesFromResource(R.xml.pref_general)
            val resetPref = findPreference(KEY_PREF_RESET)
            if (resetPref != null) {
                resetPref.onPreferenceClickListener = OnPreferenceClickListener {
                    AlertDialog.Builder(activity)
                        .setMessage(getString(R.string.pref_reset_warning))
                        .setPositiveButton(getString(R.string.confirm)) { dialog, which -> resetApp() }
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show()
                    true
                }
            }
        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
            checkPermission()
            if (key == KEY_PREF_REMINDER) {
                val enabled = sharedPreferences.getBoolean(KEY_PREF_REMINDER, false)
                if (enabled) {
                    NotificationJobService.scheduleJob(activity.applicationContext)
                } else {
                    NotificationJobService.cancelJob(activity.applicationContext)
                }
            } else if (key == KEY_PREF_REMINDER_TIME) {
                val enabled = sharedPreferences.getBoolean(KEY_PREF_REMINDER, false)
                if (enabled) {
                    NotificationJobService.cancelJob(activity.applicationContext)
                    NotificationJobService.scheduleJob(activity.applicationContext)
                }
            }
        }

        private fun checkPermission(): Boolean {
            //Check for notification permission and exact alarm permission
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                        && ContextCompat.checkSelfPermission(this.context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            ) {
                AlertDialog.Builder(this.context)
                    .setMessage(R.string.dialog_need_permission_for_notifications)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        ActivityCompat.requestPermissions(this.activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATION)
                    }
                    .setTitle(R.string.dialog_need_permission_for_notifications_title)
                    .setCancelable(true)
                    .create()
                    .show()
                return false
            }
            return true
        }

        private fun resetApp() {
            runBlocking {
                launch(Dispatchers.IO) {
                    PainDiaryDatabaseService.getInstance(activity).reinitializeDatabase(activity)
                }
            }
            resetPreferences()
        }

        private fun resetPreferences() {
            PreferenceManager.getDefaultSharedPreferences(activity.applicationContext).edit()
                .clear().commit()
            PreferenceManager.setDefaultValues(
                activity.applicationContext,
                R.xml.pref_general,
                true
            )
            NotificationJobService.cancelJob(activity.applicationContext)
            PrefManager(activity.applicationContext).userID =
                AbstractPersistentObject.INVALID_OBJECT_ID
            preferenceScreen.removeAll()
            initPreferences()
        }
    }

    companion object {
        private const val KEY_PREF_RESET = "pref_reset"
        const val KEY_PREF_MEDICATION = "pref_medication"
        private const val KEY_PREF_REMINDER = "pref_reminder"
        private const val KEY_PREF_REMINDER_TIME = "pref_reminder_time"
    }
}