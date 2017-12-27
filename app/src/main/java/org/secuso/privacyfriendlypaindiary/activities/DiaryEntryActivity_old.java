package org.secuso.privacyfriendlypaindiary.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.DBServiceInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.BodyRegion;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Condition;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.PainQuality;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Time;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.DiaryEntry;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.PainDescription;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.PainDescriptionInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;

/**
 * @author Susanne Felsen
 * @version 20171205
 */

public class DiaryEntryActivity_old extends AppCompatActivity {

    private static final String TAG = DiaryEntryActivity_old.class.getSimpleName();
    private static final int COLOR_MIDDLEBLUE = Color.parseColor("#8aa5ce");

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private Date date;

    private Button btnSave;
    private Button btnCancel;

    private RadioButton[] conditions;
    private Condition condition;
    private String notes;
    private int painLevel = 0;
    private BodyRegion bodyRegion;
    private EnumSet<PainQuality> painQualities = EnumSet.noneOf(PainQuality.class);
    private EnumSet<Time> timesOfPain = EnumSet.noneOf(Time.class);

    private DiaryEntryInterface diaryEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diaryentry);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        btnCancel = (Button) findViewById(R.id.btn_cancel);
//        btnNext = (Button) findViewById(R.id.btn_next);

//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick (View v){
//                launchHomeScreen();
//            }
//        });

        date = Calendar.getInstance().getTime();
        ((EditText) findViewById(R.id.date)).setText(dateFormat.format(date));
        diaryEntry = new DiaryEntry(date);

    }

    private void loadData() {
        ((EditText) findViewById(R.id.date)).setText(dateFormat.format(date));

        initConditions();
        if(condition != null) {
            conditions[condition.getValue()].setBackgroundColor(COLOR_MIDDLEBLUE);
        }

        SeekBar seekBar = (SeekBar) findViewById(R.id.painlevel_seekbar);
        seekBar.setProgress(painLevel);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) painLevel = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        if(painQualities.contains(PainQuality.STABBING)) {
            ((CheckBox) findViewById(R.id.pain_stabbing)).setChecked(true);
        }
        if(painQualities.contains(PainQuality.DULL)) {
            ((CheckBox) findViewById(R.id.pain_dull)).setChecked(true);
        }
        if(painQualities.contains(PainQuality.SHOOTING)) {
            ((CheckBox) findViewById(R.id.pain_shooting)).setChecked(true);
        }

        if(timesOfPain.contains(Time.ALL_DAY)) {
            ((CheckBox) findViewById(R.id.time_all_day)).setChecked(true);
        }
        if(timesOfPain.contains(Time.MORNING)) {
            ((CheckBox) findViewById(R.id.time_morning)).setChecked(true);
        }
        if(timesOfPain.contains(Time.AFTERNOON)) {
            ((CheckBox) findViewById(R.id.time_afternoon)).setChecked(true);
        }
        if(timesOfPain.contains(Time.EVENING)) {
            ((CheckBox) findViewById(R.id.time_evening)).setChecked(true);
        }
    }

    private void initConditions() {
        conditions = new RadioButton[]{
                (RadioButton) findViewById(R.id.condition_very_bad),
                (RadioButton) findViewById(R.id.condition_bad),
                (RadioButton) findViewById(R.id.condition_okay),
                (RadioButton) findViewById(R.id.condition_good),
                (RadioButton) findViewById(R.id.condition_very_good)};
    }

    private void launchHomeScreen() {
        Intent intent = new Intent(DiaryEntryActivity_old.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void selectCondition(View view){
        if(conditions == null) initConditions();
        for (int i = 0; i < conditions.length ; i++){
            if(conditions[i].isChecked()){
                conditions[i].setBackgroundColor(COLOR_MIDDLEBLUE);
                condition = Condition.valueOf(i);
            } else {
                conditions[i].setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    public void onPainQualityClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        PainQuality quality = null;
        switch(view.getId()) {
            case R.id.pain_stabbing:
                quality = PainQuality.STABBING;
                break;
            case R.id.pain_dull:
                quality = PainQuality.DULL;
                break;
            case R.id.pain_shooting:
                quality = PainQuality.SHOOTING;
                break;
            default:
                break;
        }
        if(checked && quality != null) {
            painQualities.add(quality);
        } else {
            painQualities.remove(quality);
        }
    }

    public void onTimeClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        Time time = null;
        switch(view.getId()) {
            case R.id.time_all_day:
                time = Time.ALL_DAY;
                break;
            case R.id.time_morning:
                time = Time.MORNING;
                break;
            case R.id.time_afternoon:
                time = Time.AFTERNOON;
                break;
            case R.id.time_evening:
                time = Time.EVENING;
                break;
            default:
                break;
        }
        if(checked && time != null) {
            timesOfPain.add(time);
        } else {
            timesOfPain.remove(time);
        }
    }

    public void openDatePicker(View v) {
        Calendar cal = Calendar.getInstance();
        if(date != null) cal.setTime(date);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, dayOfMonth);
                        date = c.getTime();
                        ((EditText) findViewById(R.id.date)).setText(dateFormat.format(date));
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    public void save() {
        diaryEntry.setCondition(condition);
        diaryEntry.setNotes(notes);

        PainDescriptionInterface painDescription = new PainDescription(painLevel, bodyRegion, painQualities, timesOfPain);
        diaryEntry.setPainDescription(painDescription);

        DBServiceInterface service = DBService.getInstance(this);
        long entryID = service.storeDiaryEntryAndAssociatedObjects(diaryEntry);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.diaryentry_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                Log.d(TAG, "Diary entry saved.");
//                save();
                launchHomeScreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.warning_leaving))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

}
