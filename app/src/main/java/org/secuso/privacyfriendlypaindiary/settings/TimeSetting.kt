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
package org.secuso.privacyfriendlypaindiary.settings

import android.content.res.Resources
import android.os.Build
import android.text.format.DateFormat
import android.util.TypedValue
import android.view.Gravity
import android.widget.TimePicker
import androidx.lifecycle.LiveData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import org.secuso.pfacore.model.preferences.settings.ISettingData
import org.secuso.pfacore.model.preferences.settings.ISettingDataBuildInfo
import org.secuso.pfacore.model.preferences.settings.Setting
import org.secuso.pfacore.model.preferences.settings.SettingData
import org.secuso.pfacore.model.preferences.settings.SettingDataBuildInfo
import org.secuso.pfacore.model.preferences.settings.SettingFactory
import org.secuso.pfacore.model.preferences.settings.settingDataFactory
import org.secuso.pfacore.ui.Inflatable
import org.secuso.pfacore.ui.preferences.settings.DisplaySetting
import org.secuso.pfacore.ui.preferences.settings.InflatableSetting
import org.secuso.pfacore.ui.preferences.settings.InflatableSettingInfo
import org.secuso.privacyfriendlypaindiary.R
import java.util.Calendar

private const val MILLIS_PER_HOUR = 60 * 60 * 1000
private const val MILLIS_PER_MINUTE = 60 * 1000

/**
 * A time-of-day setting for the PFA-Core settings menu. PFA-Core ships switch, radio and menu
 * settings, but no time picker, so this mirrors their structure to add one. The value is stored
 * as the number of milliseconds since midnight (an Int) to stay within the types the preference
 * backup supports.
 *
 * The bare model part keeps the data, the concrete class below adds the view representation: a
 * tappable label showing the chosen time that opens a [TimePicker] dialog.
 */
abstract class TimeSettingBase<SD : TimeSettingBase.TimeData>(override val data: SD) : Setting<SD> {
    open class TimeData(val data: SettingData<Int>) : ISettingData<Int> by data
    interface TimeBuildInfo : ISettingDataBuildInfo<Int>

    companion object {
        fun <SI : TimeBuildInfo, SD : TimeData> factory(adapt: (SI, TimeData) -> SD): SettingFactory<SI, SD> =
            settingDataFactory { info, data -> adapt(info, TimeData(data)) }
    }
}

class TimeSetting(data: TimeData) : TimeSettingBase<TimeSetting.TimeData>(data), InflatableSettingInfo {

    companion object {
        fun factory(): SettingFactory<TimeBuildInfo, TimeData> =
            TimeSettingBase.factory { info, data ->
                val title = info.title ?: throw IllegalStateException("This setting requires a title")
                val summary = info.summary ?: throw IllegalStateException("This setting requires a summary")
                TimeData(data.data, title, summary)
            }
    }

    class TimeData(
        data: SettingData<Int>,
        val title: (TimeData, Int) -> Inflatable,
        val summary: (TimeData, Int) -> Inflatable,
    ) : TimeSettingBase.TimeData(data) {
        fun create() = TimeSetting(this)
    }

    class TimeBuildInfo(resources: Resources, data: SettingDataBuildInfo<Int> = SettingDataBuildInfo()) :
        DisplaySetting<Int, TimeData>(resources), TimeSettingBase.TimeBuildInfo, ISettingDataBuildInfo<Int> by data

    override val enabled: LiveData<Boolean>
        get() = data.enabled
    override val expandable: Boolean
        get() = false
    override val title: Inflatable
        get() = data.title(data, data.state.value ?: data.default)
    override val description: Inflatable
        get() = data.summary(data, data.state.value ?: data.default)
    override val action: Inflatable
        get() = Inflatable { inflater, _, owner ->
            val context = inflater.context
            MaterialTextView(context).apply {
                gravity = Gravity.CENTER
                setPadding(paddingDp(context, 8), 0, paddingDp(context, 8), 0)
                isClickable = true
                isFocusable = true
                val outValue = TypedValue()
                context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
                setBackgroundResource(outValue.resourceId)
                text = formatTime(context, data.value)
                data.state.observe(owner) { text = formatTime(context, it) }
                // A disabled view does not dispatch clicks, so this also blocks the picker
                // while the setting is greyed out by its dependency.
                isEnabled = data.enabled.value ?: true
                data.enabled.observe(owner) { isEnabled = it }
                setOnClickListener { showPicker(context) }
            }
        }

    private fun showPicker(context: android.content.Context) {
        val current = data.value
        val picker = TimePicker(context).apply {
            setIs24HourView(DateFormat.is24HourFormat(context))
            setPickerHour(this, current / MILLIS_PER_HOUR)
            setPickerMinute(this, (current / MILLIS_PER_MINUTE) % 60)
        }
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.pref_reminder_time)
            .setView(picker)
            .setPositiveButton(R.string.set) { _, _ ->
                data.value = pickerHour(picker) * MILLIS_PER_HOUR + pickerMinute(picker) * MILLIS_PER_MINUTE
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun formatTime(context: android.content.Context, millisOfDay: Int): CharSequence {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, millisOfDay / MILLIS_PER_HOUR)
            set(Calendar.MINUTE, (millisOfDay / MILLIS_PER_MINUTE) % 60)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return DateFormat.getTimeFormat(context).format(calendar.time)
    }

    private fun paddingDp(context: android.content.Context, dp: Int) =
        (dp * context.resources.displayMetrics.density).toInt()

    private fun setPickerHour(picker: TimePicker, hour: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) picker.hour = hour else @Suppress("DEPRECATION") run { picker.currentHour = hour }
    }

    private fun setPickerMinute(picker: TimePicker, minute: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) picker.minute = minute else @Suppress("DEPRECATION") run { picker.currentMinute = minute }
    }

    private fun pickerHour(picker: TimePicker) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) picker.hour else @Suppress("DEPRECATION") picker.currentHour

    private fun pickerMinute(picker: TimePicker) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) picker.minute else @Suppress("DEPRECATION") picker.currentMinute
}

/**
 * Adds a [TimeSetting] to the settings menu, mirroring the switch/radio builders shipped with
 * PFA-Core.
 */
fun InflatableSetting.time(initializer: TimeSetting.TimeBuildInfo.() -> Unit): TimeSetting.TimeData {
    return TimeSetting.TimeBuildInfo(context.resources).apply(initializer)
        .build(TimeSetting.factory())
        .create()
        .register()
        .data
}
