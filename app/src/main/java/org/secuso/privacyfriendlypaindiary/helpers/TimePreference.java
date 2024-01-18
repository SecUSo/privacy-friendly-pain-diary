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
package org.secuso.privacyfriendlypaindiary.helpers;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import org.secuso.privacyfriendlypaindiary.R;

import java.util.Calendar;

/**
 * Preference dialog that allows the user to pick a time in hour:minute format. The value is stored
 * as timestamp in milliseconds in preferences and can be easily converted into a Calendar object.
 *
 * @author Susanne Felsen (code copied from <a href="https://github.com/SecUSo/privacy-friendly-pedometer/blob/master/app/src/main/java/org/secuso/privacyfriendlyactivitytracker/preferences/TimePreference.java">Privacy Friendly Pedometer</a>
 * @version 20180130
 */

public class TimePreference extends DialogPreference {
    private Calendar calendar;
    private TimePicker picker = null;

    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPositiveButtonText(R.string.set);
        setNegativeButtonText(android.R.string.cancel);
        calendar = Calendar.getInstance();
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        return picker;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        setHour(picker, calendar.get(Calendar.HOUR_OF_DAY));
        setMinute(picker, calendar.get(Calendar.MINUTE));
        picker.setIs24HourView(DateFormat.is24HourFormat(getContext()));
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            calendar.set(Calendar.HOUR_OF_DAY, getHour(picker));
            calendar.set(Calendar.MINUTE, getMinute(picker));

            setSummary(getSummary());
            if (callChangeListener(calendar.getTimeInMillis())) {
                persistLong(calendar.getTimeInMillis());
                notifyChanged();
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if (defaultValue == null) {
            calendar.setTimeInMillis(getPersistedLong(System.currentTimeMillis()));
        } else if (restoreValue) {
            calendar.setTimeInMillis(Long.parseLong(getPersistedString((String) defaultValue)));
        } else {
            calendar.setTimeInMillis(Long.parseLong((String) defaultValue));
        }
        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {
        if (calendar == null) {
            return null;
        }
        return DateFormat.getTimeFormat(getContext()).format(calendar.getTime());
    }

    private void setHour(TimePicker timePicker, int hour) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(hour);
        } else {
            timePicker.setCurrentHour(hour);
        }
    }

    private int getHour(TimePicker timePicker) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return timePicker.getHour();
        } else {
            return timePicker.getCurrentHour();
        }
    }

    private void setMinute(TimePicker timePicker, int minute) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setMinute(minute);
        } else {
            timePicker.setCurrentMinute(minute);
        }
    }

    private int getMinute(TimePicker timePicker) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return timePicker.getMinute();
        } else {
            return timePicker.getCurrentMinute();
        }
    }

}
