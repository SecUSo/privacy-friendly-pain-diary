package org.secuso.privacyfriendlypaindiary

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import org.secuso.pfacore.model.Theme
import org.secuso.pfacore.model.about.About
import org.secuso.pfacore.model.preferences.Preferable
import org.secuso.pfacore.model.preferences.settings.ISettingData
import org.secuso.pfacore.ui.PFData
import org.secuso.pfacore.ui.help.Help
import org.secuso.pfacore.ui.preferences.appPreferences
import org.secuso.pfacore.ui.preferences.settings.appearance
import org.secuso.pfacore.ui.preferences.settings.preferenceFirstTimeLaunch
import org.secuso.pfacore.ui.preferences.settings.settingDeviceInformationOnErrorReport
import org.secuso.pfacore.ui.preferences.settings.settingThemeSelector
import org.secuso.pfacore.ui.preferences.settings.switch
import org.secuso.pfacore.ui.tutorial.buildTutorial
import org.secuso.privacyfriendlypaindiary.helpers.NotificationJobService
import org.secuso.privacyfriendlypaindiary.settings.time

/**
 * Single source of truth for the data the PFA-Core empty-shell needs (preferences,
 * settings, about and tutorial). Built once and exposed as a singleton.
 */
class PFApplicationData private constructor(context: Context) {

    lateinit var theme: ISettingData<String>
        private set
    lateinit var firstTimeLaunch: Preferable<Boolean>
        private set
    lateinit var includeDeviceDataInReport: Preferable<Boolean>
        private set
    lateinit var medicationEnabled: Preferable<Boolean>
        private set
    lateinit var reminderEnabled: Preferable<Boolean>
        private set
    lateinit var reminderTime: Preferable<Int>
        private set
    lateinit var userId: Preferable<Int>
        private set

    private val preferences = appPreferences(context) {
        preferences {
            firstTimeLaunch = preferenceFirstTimeLaunch
            // The user id links the saved diary to the stored user details. It lives in the
            // default preferences (not its own file anymore) and is backed up, so a restore brings
            // the user's name, birthday and so on back together with the database.
            userId = preference {2
                key = "userID"
                default = 0
                backup = true
            }
        }
        settings {
            category(R.string.pref_header_general) {
                medicationEnabled = switch {
                    key = "pref_medication"
                    default = true
                    backup = true
                    title { resource(R.string.pref_medication) }
                    summary { resource(R.string.pref_medication_summary) }
                }
            }
            category(R.string.pref_header_notifications) {
                reminderEnabled = switch {
                    key = "pref_reminder"
                    default = false
                    backup = true
                    title { resource(R.string.pref_reminder) }
                    summary { literal("") }
                    // React to the daily reminder being turned on or off without an activity
                    // having to listen for the change.
                    onUpdate = { enabled ->
                        if (enabled) {
                            NotificationJobService.scheduleJob(context)
                        } else {
                            NotificationJobService.cancelJob(context)
                        }
                    }
                }
                reminderTime = time {
                    key = "pref_reminder_time"
                    default = DEFAULT_REMINDER_MILLIS_OF_DAY
                    backup = true
                    title { resource(R.string.pref_reminder_time) }
                    summary { literal("") }
                    // Only selectable while the reminder is active.
                    dependency = { "pref_reminder" on true }
                    // Reschedule the reminder so it fires at the new time.
                    onUpdate = {
                        if (reminderEnabled.value) {
                            NotificationJobService.cancelJob(context)
                            NotificationJobService.scheduleJob(context)
                        }
                    }
                }
            }
            appearance {
                theme = settingThemeSelector
            }
            category("Error Report") {
                includeDeviceDataInReport = settingDeviceInformationOnErrorReport
            }
        }
    }

    private val about = About(
        name = context.resources.getString(R.string.app_name),
        version = BuildConfig.VERSION_NAME,
        authors = context.resources.getString(R.string.about_author_names),
        repo = "https://github.com/SecUSo/privacy-friendly-pain-diary"
    )

    private val help = Help.build(context) {
        item {
            title { resource(R.string.help_whatis) }
            description { resource(R.string.help_whatis_answer) }
        }
        item {
            title { resource(R.string.help_privacy) }
            description { resource(R.string.help_privacy_answer) }
        }
        item {
            title { resource(R.string.help_permission) }
            description { resource(R.string.help_permission_answer) }
        }
        item {
            title { resource(R.string.help_add_entry) }
            description { resource(R.string.help_add_entry_answer) }
        }
        item {
            title { resource(R.string.help_entry_information) }
            description { resource(R.string.help_entry_information_answer) }
        }
        item {
            title { resource(R.string.help_userdetails) }
            description { resource(R.string.help_userdetails_answer) }
        }
        item {
            title { resource(R.string.help_pdf_export) }
            description { resource(R.string.help_pdf_export_answer) }
        }
    }

    private val tutorial = buildTutorial {
        stage {
            title = context.getString(R.string.slide1_heading)
            description = context.getString(R.string.slide1_text)
        }
        stage {
            title = context.getString(R.string.slide2_heading)
            description = context.getString(R.string.slide2_text)
        }
        stage {
            title = context.getString(R.string.slide3_heading)
            description = context.getString(R.string.slide3_text)
        }
        stage {
            title = context.getString(R.string.slide4_heading)
            description = context.getString(R.string.slide4_text)
        }
    }

    val data: PFData = PFData(
        preferences = preferences,
        about = about,
        help = help,
        tutorial = tutorial,
        theme = theme.state.map { Theme.valueOf(it) },
        firstLaunch = firstTimeLaunch,
        includeDeviceDataInReport = includeDeviceDataInReport,
    )

    companion object {
        // 18:00 expressed as milliseconds since midnight.
        private const val DEFAULT_REMINDER_MILLIS_OF_DAY = 18 * 60 * 60 * 1000

        private var _instance: PFApplicationData? = null

        @JvmStatic
        fun instance(context: Context): PFApplicationData {
            if (_instance == null) {
                _instance = PFApplicationData(context.applicationContext)
            }
            return _instance!!
        }
    }
}
