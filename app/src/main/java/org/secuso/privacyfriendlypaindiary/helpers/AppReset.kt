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

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import org.secuso.pfacore.model.dialog.AbortElseDialog
import org.secuso.pfacore.ui.dialog.show
import org.secuso.privacyfriendlypaindiary.R
import org.secuso.privacyfriendlypaindiary.activities.MainActivity
import org.secuso.privacyfriendlypaindiary.database.PainDiaryDatabaseService
import org.secuso.privacyfriendlypaindiary.database.entities.impl.AbstractPersistentObject
import org.secuso.privacyfriendlypaindiary.tutorial.PrefManager

/**
 * Wipes all diary entries and user details. It is triggered from the PFA-Core settings screen
 * through an action setting, so the work lives here instead of in an activity.
 */
object AppReset {

    fun confirmAndReset(activity: AppCompatActivity) {
        AbortElseDialog.build(activity) {
            title = { activity.getString(R.string.pref_reset) }
            content = { activity.getString(R.string.pref_reset_warning) }
            acceptLabel = activity.getString(R.string.confirm)
            abortLabel = activity.getString(R.string.cancel)
            onElse = { performReset(activity) }
        }.show()
    }

    private fun performReset(activity: AppCompatActivity) {
        val context = activity.applicationContext
        // The database reset touches disk, so keep it off the main thread.
        Thread {
            PainDiaryDatabaseService.getInstance(context).reinitializeDatabase(context)
            PrefManager(context).userID = AbstractPersistentObject.INVALID_OBJECT_ID
            activity.runOnUiThread {
          
                activity.startActivity(
                    Intent(activity, MainActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
        }.start()
    }
}
