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
package org.secuso.privacyfriendlypaindiary.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.DBServiceInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlypaindiary.helpers.EventDecorator;
import org.secuso.privacyfriendlypaindiary.helpers.Helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This activity corresponds to the main screen of the application which shows
 * a calendar. By selecting a day in that calendar a user can create a new diary
 * entry or view an existing one. Days for which entries exist are highlighted.
 * Existing entries can be edited or deleted.
 *
 * @author Susanne Felsen
 * @version 20180228
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int COLOR_MIDDLEBLUE = Color.parseColor("#8aa5ce");

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private MaterialCalendarView calendar;
    private EventDecorator decorator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String dateAsString = getIntent().getStringExtra("DATE_TO_DISPLAY");
        if (dateAsString == null) {
            dateAsString = dateFormat.format(Calendar.getInstance().getTime());
        }
        Date date;
        try {
            date = dateFormat.parse(dateAsString);
        } catch (ParseException e) {
            date = Calendar.getInstance().getTime();
        }

        calendar = findViewById(R.id.calendar_view);
        decorator = new EventDecorator(COLOR_MIDDLEBLUE);
        calendar.addDecorator(decorator);
        calendar.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                getDiaryEntryDates(date.getMonth(), date.getYear());
            }
        });
        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                //checks whether there is already an entry on this date, creates one if there is not
                if(!decorator.shouldDecorate(date)) {
                    createDiaryEntry(date.getDate());
                } else {
                    viewDiaryEntry(date.getDate());
                }
            }
        });
        calendar.setCurrentDate(date);
        calendar.setSelectedDate(date);
    }

    @Override
    public void onResume() {
        super.onResume();
        calendar.state().edit()
                .setMaximumDate(CalendarDay.today())
                .commit();
        getDiaryEntryDates(calendar.getCurrentDate().getMonth(), calendar.getCurrentDate().getYear());

    }

    private void getDiaryEntryDates(int month, int year) {
        DBServiceInterface service = DBService.getInstance(this);

        Calendar c = Calendar.getInstance();
        if (month > 0) {
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month - 1);
        } else {
            c.set(Calendar.YEAR, year - 1);
            c.set(Calendar.MONTH, 11);
        }
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH) - 6);
        Date startDate = c.getTime();
        if (month < 11) {
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month + 1);
        } else {
            c.set(Calendar.YEAR, year + 1);
            c.set(Calendar.MONTH, 0);
        }
        c.set(Calendar.DAY_OF_MONTH, 7);
        Date endDate = c.getTime();
        Set<Date> dates = service.getDiaryEntryDatesByTimeSpan(startDate, endDate);
        Set<CalendarDay> calendarDates = new HashSet<>();
        for (Date date : dates) {
            calendarDates.add(CalendarDay.from(date));
        }
        decorator.setDates(calendarDates);
        calendar.invalidateDecorators();
    }

    /**
     * This method connects the Activity to the menu item
     *
     * @return ID of the menu item it belongs to
     */
    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_main;
    }

    private void createDiaryEntry(Date date) {
        Intent intent = new Intent(MainActivity.this, DiaryEntryActivity.class);
        intent.putExtra("DATE_OF_ENTRY", dateFormat.format(date));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void viewDiaryEntry(final Date date) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        DBServiceInterface service = DBService.getInstance(this);
        DiaryEntryInterface diaryEntry = service.getDiaryEntryByDate(date);
        alertDialog.setView(Helper.getDiaryEntrySummary(this, diaryEntry));
        alertDialog.setPositiveButton("OK", null);
        alertDialog.setNegativeButton(getString(R.string.edit),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        editDiaryEntry(date);
                    }
                });
        alertDialog.setNeutralButton(getString(R.string.delete),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage(getString(R.string.warning_deleting))
                                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteDiaryEntry(date);
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(date);
                                        getDiaryEntryDates(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
                                        calendar.invalidate();
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        viewDiaryEntry(date);
                                    }
                                })
                                .show();
                    }
                });
        alertDialog.create().show();
    }

    private void editDiaryEntry(Date date) {
        Intent intent = new Intent(MainActivity.this, DiaryEntryActivity.class);
        intent.putExtra("DATE_OF_ENTRY", dateFormat.format(date));
        intent.putExtra("EDIT", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void deleteDiaryEntry(Date date) {
        DBServiceInterface service = DBService.getInstance(this);
        DiaryEntryInterface diaryEntry = service.getDiaryEntryByDate(date);
        service.deleteDiaryEntryAndAssociatedObjects(diaryEntry);
    }

}