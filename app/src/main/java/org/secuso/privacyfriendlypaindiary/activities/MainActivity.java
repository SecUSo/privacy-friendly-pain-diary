/*
 This file is part of Privacy Friendly App Example.

 Privacy Friendly App Example is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.

 Privacy Friendly App Example is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Privacy Friendly App Example. If not, see <http://www.gnu.org/licenses/>.
 */

package org.secuso.privacyfriendlypaindiary.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.DBServiceInterface;
import org.secuso.privacyfriendlypaindiary.helpers.EventDecorator;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Christopher Beckmann, Karola Marky, Susanne Felsen
 * @version 20171205
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MaterialCalendarView calendar;
    private EventDecorator decorator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = (MaterialCalendarView) findViewById(R.id.calendar_view);
        calendar.setSelectedDate(CalendarDay.today());
        decorator = new EventDecorator(Color.parseColor("#8aa5ce"));
        calendar.addDecorator(decorator);
        getDiaryEntryDates(calendar.getSelectedDate().getMonth(), calendar.getSelectedDate().getYear());
        calendar.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                getDiaryEntryDates(date.getMonth(), date.getYear());
            }
        });
//        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
//
//            @Override
//            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
//
//            }
//        });
    }

    private void getDiaryEntryDates(int month, int year) {
        DBServiceInterface service = DBService.getInstance(this);

        Calendar c = Calendar.getInstance();
        if(month > 0) {
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month - 1);
        } else {
            c.set(Calendar.YEAR, year - 1);
            c.set(Calendar.MONTH, 11);
        }
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH) - 6);
        Date startDate = c.getTime();
        if(month < 11) {
            c.set(Calendar.MONTH, month + 1);
        } else {
            c.set(Calendar.YEAR, year + 1);
            c.set(Calendar.MONTH, 0);
        }
        c.set(Calendar.DAY_OF_MONTH, 7);
        Date endDate = c.getTime();

        Set<Date> dates = service.getDiaryEntryDatesByTimeSpan(startDate, endDate);
        Set<CalendarDay> calendarDates = new HashSet<>();
        for(Date date : dates) {
            calendarDates.add(CalendarDay.from(date));
        }
        decorator.setDates(calendarDates);
        calendar.invalidateDecorators();
    }

    /**
     * This method connects the Activity to the menu item
     * @return ID of the menu item it belongs to
     */
    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_main;
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_add_entry:
                Intent intent = new Intent(MainActivity.this, DiaryEntryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}