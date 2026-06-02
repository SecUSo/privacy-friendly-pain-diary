package org.secuso.privacyfriendlypaindiary

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import org.secuso.pfacore.model.Theme
import org.secuso.pfacore.model.about.About
import org.secuso.pfacore.model.preferences.Preferable
import org.secuso.pfacore.model.preferences.settings.ISettingData
import org.secuso.pfacore.ui.PFData
import org.secuso.pfacore.ui.preferences.appPreferences
import org.secuso.pfacore.ui.preferences.settings.appearance
import org.secuso.pfacore.ui.preferences.settings.preferenceFirstTimeLaunch
import org.secuso.pfacore.ui.preferences.settings.settingDeviceInformationOnErrorReport
import org.secuso.pfacore.ui.preferences.settings.settingThemeSelector
import org.secuso.pfacore.ui.tutorial.buildTutorial

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

    private val preferences = appPreferences(context) {
        preferences {
            firstTimeLaunch = preferenceFirstTimeLaunch
            medicationEnabled = preference {
                key = "pref_medication"
                default = true
                backup = true
            }
            reminderEnabled = preference {
                key = "pref_reminder"
                default = false
                backup = true
            }
            reminderTime = preference {
                key = "pref_reminder_time"
                default = 64800000
                backup = true
            }
        }
        settings {
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
        tutorial = tutorial,
        theme = theme.state.map { Theme.valueOf(it) },
        firstLaunch = firstTimeLaunch,
        includeDeviceDataInReport = includeDeviceDataInReport,
    )

    companion object {
        private var _instance: PFApplicationData? = null
        fun instance(context: Context): PFApplicationData {
            if (_instance == null) {
                _instance = PFApplicationData(context.applicationContext)
            }
            return _instance!!
        }
    }
}
