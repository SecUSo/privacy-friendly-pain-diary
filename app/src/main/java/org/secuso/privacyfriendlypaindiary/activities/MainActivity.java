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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.DBServiceInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.BodyRegion;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Condition;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.PainQuality;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Time;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugIntakeInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.PainDescriptionInterface;
import org.secuso.privacyfriendlypaindiary.helpers.EventDecorator;
import org.secuso.privacyfriendlypaindiary.helpers.Helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Christopher Beckmann, Karola Marky, Susanne Felsen
 * @version 20180110
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int COLOR_MIDDLEBLUE = Color.parseColor("#8aa5ce");

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

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
        alertDialog.setView(initDiaryEntrySummary(date));
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

    private View initDiaryEntrySummary(Date date) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.diaryentry_summary, null);

        DBServiceInterface service = DBService.getInstance(this);
        DiaryEntryInterface diaryEntry = service.getDiaryEntryByDate(date);
        PainDescriptionInterface painDescription = diaryEntry.getPainDescription();

        ((TextView) view.findViewById(R.id.date)).setText(dateFormat.format(date));
        if(diaryEntry.getNotes() != null) {
            ((TextView) view.findViewById(R.id.notes_value)).setText(diaryEntry.getNotes());
        }
        if(diaryEntry.getCondition() != null) {
            ((ImageView) view.findViewById(R.id.condition_icon)).setImageResource(getResourceIDForCondition(diaryEntry.getCondition()));
        }
        if(painDescription != null) {
            ((TextView) view.findViewById(R.id.painlevel_value)).setText(Integer.toString(painDescription.getPainLevel()));
            EnumSet<BodyRegion> bodyRegions = painDescription.getBodyRegions();
            if(!bodyRegions.isEmpty()) {
                Bitmap[] images = getBitmapArrayForBodyRegions(bodyRegions);
                ((ImageView) view.findViewById(R.id.bodyregion_value)).setImageBitmap(Helper.overlay(images));
                view.findViewById(R.id.bodyregion_value).setVisibility(View.VISIBLE);
            }
            String painQualities = convertPainQualityEnumSetToString(painDescription.getPainQualities());
            if(painQualities != null) {
                ((TextView) view.findViewById(R.id.painquality_value)).setText(painQualities);
            }
            String timesOfPain = convertTimeEnumSetToString(painDescription.getTimesOfPain());
            if(timesOfPain != null) {
                ((TextView) view.findViewById(R.id.timeofpain_value)).setText(timesOfPain);
            }
        }
        String medication = "";
        for(DrugIntakeInterface drugIntake : diaryEntry.getDrugIntakes()) {
            medication = medication + drugIntake.getDrug().getName() + " (" + drugIntake.getDrug().getDose() + ") " +
                drugIntake.getQuantityMorning() + " " + drugIntake.getQuantityNoon() + " " + drugIntake.getQuantityEvening() + " " + drugIntake.getQuantityNight() +
                System.getProperty("line.separator");
        }
        if(!medication.isEmpty()) {
            ((TextView) view.findViewById(R.id.medication_value)).setText(medication);
        }

        //add view to Helper View list
        Helper.addViewExportPdf(view);

        return view;
    }

    private Bitmap[] getBitmapArrayForBodyRegions(EnumSet<BodyRegion> bodyRegions) {
        Bitmap[] images = new Bitmap[bodyRegions.size()];
        int i = 0;
        for(BodyRegion region : bodyRegions) {
            int resourceID = Helper.getResourceIDForBodyRegion(region);
            if (resourceID != 0) {
                images[i] = BitmapFactory.decodeResource(getResources(), resourceID);
                i++;
            }
        }
        return images;
    }

    private int getResourceIDForCondition(Condition condition) {
        switch(condition) {
            case VERY_BAD:
                return R.drawable.ic_sentiment_very_dissatisfied;
            case BAD:
                return R.drawable.ic_sentiment_dissatisfied;
            case OKAY:
                return R.drawable.ic_sentiment_neutral;
            case GOOD:
                return R.drawable.ic_sentiment_satisfied;
            case VERY_GOOD:
                return R.drawable.ic_sentiment_very_satisfied;
            default:
                return R.drawable.ic_sentiment_neutral;
        }
    }

    private String convertPainQualityEnumSetToString(EnumSet<PainQuality> painQualities) {
        String painQualitiesAsString = "";
        for(PainQuality quality : painQualities) {
            painQualitiesAsString += getString(quality.getResourceID()) + ", ";
        }
        if(!painQualitiesAsString.isEmpty()) {
            painQualitiesAsString = painQualitiesAsString.substring(0, painQualitiesAsString.length() - 2);
        } else {
            painQualitiesAsString = null;
        }
        return painQualitiesAsString;
    }

    private String convertTimeEnumSetToString(EnumSet<Time> times) {
        String timesAsString = "";
        for(Time time : times) {
            timesAsString += getString(time.getResourceID()) + ", ";
        }
        if(!timesAsString.isEmpty()) {
            timesAsString = timesAsString.substring(0, timesAsString.length() - 2);
        } else {
            timesAsString = null;
        }
        return timesAsString;
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