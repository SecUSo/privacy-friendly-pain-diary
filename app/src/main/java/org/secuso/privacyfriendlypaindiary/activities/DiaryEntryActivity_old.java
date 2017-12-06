package org.secuso.privacyfriendlypaindiary.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

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
import org.secuso.privacyfriendlypaindiary.helpers.MyViewPagerAdapter;

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

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnNext;
    private Button btnCancel;

    private String dateAsString;
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

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layout_dots);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnNext = (Button) findViewById(R.id.btn_next);

        layouts = new int[] {R.layout.diaryentry_slide1, R.layout.diaryentry_slide2, R.layout.diaryentry_slide3,};

        addBottomDots(0);

        myViewPagerAdapter = new MyViewPagerAdapter(this, layouts);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                addBottomDots(position);
                if(position == 0) {
                    setDataOnFirstSlide();
                }
                if(position == 2) {
                    setDataOnThirdSlide();
                }
                if (position == layouts.length - 1) {
                    btnNext.setText(getString(R.string.save));
                } else {
                    btnNext.setText(getString(R.string.next));
                }
            }

            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                int current = getItem(+1);
                if (current < layouts.length) {
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                launchHomeScreen();
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = Calendar.getInstance().getTime();
        dateAsString = dateFormat.format(date);
        diaryEntry = new DiaryEntry(date);

        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setDataOnFirstSlide();
                viewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    private void setDataOnFirstSlide() {
        View firstSlide = (View) viewPager.findViewWithTag(0);
        ((TextView) firstSlide.findViewById(R.id.date)).setText(dateAsString);

        initConditions();
        if(condition != null) {
            conditions[condition.getValue()].setBackgroundColor(Color.parseColor("#8aa5ce"));
        }

        SeekBar seekBar = (SeekBar) firstSlide.findViewById(R.id.painlevel_seekbar);
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

    }

    private void setDataOnThirdSlide() {
        View thirdSlide = (View) viewPager.findViewWithTag(2);

        if(painQualities.contains(PainQuality.STABBING)) {
            ((CheckBox) thirdSlide.findViewById(R.id.pain_stabbing)).setChecked(true);
        }
        if(painQualities.contains(PainQuality.DULL)) {
            ((CheckBox) thirdSlide.findViewById(R.id.pain_dull)).setChecked(true);
        }
        if(painQualities.contains(PainQuality.SHOOTING)) {
            ((CheckBox) thirdSlide.findViewById(R.id.pain_shooting)).setChecked(true);
        }

        if(timesOfPain.contains(Time.ALL_DAY)) {
            ((CheckBox) thirdSlide.findViewById(R.id.time_all_day)).setChecked(true);
        }
        if(timesOfPain.contains(Time.MORNING)) {
            ((CheckBox) thirdSlide.findViewById(R.id.time_morning)).setChecked(true);
        }
        if(timesOfPain.contains(Time.AFTERNOON)) {
            ((CheckBox) thirdSlide.findViewById(R.id.time_afternoon)).setChecked(true);
        }
        if(timesOfPain.contains(Time.EVENING)) {
            ((CheckBox) thirdSlide.findViewById(R.id.time_evening)).setChecked(true);
        }
    }

    private void initConditions() {
        View firstSlide = (View) viewPager.findViewWithTag(0);
        conditions = new RadioButton[]{
                (RadioButton) firstSlide.findViewById(R.id.condition_very_bad),
                (RadioButton) firstSlide.findViewById(R.id.condition_bad),
                (RadioButton) firstSlide.findViewById(R.id.condition_okay),
                (RadioButton) firstSlide.findViewById(R.id.condition_good),
                (RadioButton) firstSlide.findViewById(R.id.condition_very_good)};
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
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
                conditions[i].setBackgroundColor(Color.parseColor("#8aa5ce"));
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

    public void save() {
        diaryEntry.setCondition(condition);
        diaryEntry.setNotes(notes);

        PainDescriptionInterface painDescription = new PainDescription(painLevel, bodyRegion, painQualities, timesOfPain);
        diaryEntry.setPainDescription(painDescription);

        DBServiceInterface service = DBService.getInstance(this);
        long entryID = service.storeDiaryEntryAndAssociatedObjects(diaryEntry);

    }



}
