package org.secuso.privacyfriendlypaindiary.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
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
 * @version 20171227
 */

public class DiaryEntryActivity extends AppCompatActivity {

    private static final String TAG = DiaryEntryActivity.class.getSimpleName();
    private static final int COLOR_MIDDLEBLUE = Color.parseColor("#8aa5ce");
    private static final int COLOR_YELLOW = Color.parseColor("#f6d126");

    private Button btnBack;
    private Button btnNext;

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layout_dots);
        btnBack = (Button) findViewById(R.id.btn_back);
        btnNext = (Button) findViewById(R.id.btn_next);

        layouts = new int[]{R.layout.diaryentry_slide1,
                R.layout.diaryentry_slide2,
                R.layout.diaryentry_slide3,
                R.layout.diaryentry_slide4,
                R.layout.diaryentry_slide5,
                R.layout.diaryentry_slide6,
                R.layout.diaryentry_slide7,};

        addBottomDots(0);

        myViewPagerAdapter = new MyViewPagerAdapter(this, layouts);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
                if (position == 0) {
                    setDataOnSlide1();
                } else if (position == 1) {
                    setDataOnSlide2();
                } else if (position == 2) {
                    setDataOnSlide3();
                } else if (position == 3) {
                    setDataOnSlide4();
                } else if (position == 4) {
                    setDataOnSlide5();
                } else if (position == 5) {
                    setDataOnSlide6();
                } else if (position == 6) {
                    setDataOnSlide7();
                }
                if (position == layouts.length - 1) {
                    btnNext.setText(getString(R.string.save));
                } else {
                    btnNext.setText(getString(R.string.next));
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(+1);
                if (current < layouts.length) {
                    viewPager.setCurrentItem(current);
                } else {
                    save();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(-1);
                if(current >= 0) {
                    viewPager.setCurrentItem(current);
                } else {
                   onBackPressed();
                }
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = Calendar.getInstance().getTime();
        dateAsString = dateFormat.format(date);
        diaryEntry = new DiaryEntry(date);

        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setDataOnSlide1();
                viewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    private void setDataOnSlide1() {
        View firstSlide = (View) viewPager.findViewWithTag(0);
        ((TextView) firstSlide.findViewById(R.id.date)).setText(dateAsString);

        //TODO
//        ((EditText) findViewById(R.id.date)).setText(dateFormat.format(date));

        initConditions();
        if (condition != null) {
            conditions[condition.getValue()].setBackgroundColor(COLOR_MIDDLEBLUE);
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

    private void setDataOnSlide2() {
        View slide = (View) viewPager.findViewWithTag(1);

        SeekBar seekBar = (SeekBar) slide.findViewById(R.id.painlevel_seekbar);
        seekBar.setProgress(painLevel);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) painLevel = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void setDataOnSlide3() {
        View slide = (View) viewPager.findViewWithTag(2);

        //body region
    }

    private void setDataOnSlide4() {
        View slide = (View) viewPager.findViewWithTag(3);

        if (painQualities.contains(PainQuality.STABBING)) {
            ((CheckBox) slide.findViewById(R.id.pain_stabbing)).setChecked(true);
        }
        if (painQualities.contains(PainQuality.DULL)) {
            ((CheckBox) slide.findViewById(R.id.pain_dull)).setChecked(true);
        }
        if (painQualities.contains(PainQuality.SHOOTING)) {
            ((CheckBox) slide.findViewById(R.id.pain_shooting)).setChecked(true);
        }
    }

    private void setDataOnSlide5() {
        View slide = (View) viewPager.findViewWithTag(4);

        if (timesOfPain.contains(Time.ALL_DAY)) {
            ((CheckBox) slide.findViewById(R.id.time_all_day)).setChecked(true);
        }
        if (timesOfPain.contains(Time.MORNING)) {
            ((CheckBox) slide.findViewById(R.id.time_morning)).setChecked(true);
        }
        if (timesOfPain.contains(Time.AFTERNOON)) {
            ((CheckBox) slide.findViewById(R.id.time_afternoon)).setChecked(true);
        }
        if (timesOfPain.contains(Time.EVENING)) {
            ((CheckBox) slide.findViewById(R.id.time_evening)).setChecked(true);
        }
    }

    private void setDataOnSlide6() {
        View slide = (View) viewPager.findViewWithTag(5);
        //notes
    }

    private void setDataOnSlide7() {
        View slide = (View) viewPager.findViewWithTag(6);
        //medication
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
        Intent intent = new Intent(DiaryEntryActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void selectCondition(View view) {
        if (conditions == null) initConditions();
        for (int i = 0; i < conditions.length; i++) {
            if (conditions[i].isChecked()) {
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
        switch (view.getId()) {
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
        if (checked && quality != null) {
            painQualities.add(quality);
        } else {
            painQualities.remove(quality);
        }
    }

    public void onTimeClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        Time time = null;
        switch (view.getId()) {
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
        if (checked && time != null) {
            timesOfPain.add(time);
        } else {
            timesOfPain.remove(time);
        }
    }

    public void onBodyPartClicked(View v) {

        ImageView head = (ImageView) findViewById(R.id.head);
        head.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX();
                    float y = event.getY();

                    ImageView bodyPart = getBodyPartByCoordinates(x, y);

                    if (bodyPart != null) {
                        if (bodyPart.getImageTintList() == null) {
                            paintBodyPart(true, bodyPart);
                        } else {
                            paintBodyPart(false, bodyPart);
                        }
                    }

                }
                return false;
            }

            public ImageView getBodyPartByCoordinates(float x, float y) {
                ImageView bodyPart = null;
                if (x > 545 && y > 596 && x < 614 && y < 688) {
                    bodyPart = (ImageView) findViewById(R.id.abdomen_right);
                }
                if (x > 458 && y > 596 && x < 521 && y < 688) {
                    bodyPart = (ImageView) findViewById(R.id.abdomen_left);
                }
                if (x > 435 && y > 703 && x < 518 && y < 799) {
                    bodyPart = (ImageView) findViewById(R.id.groin_left);
                }
                if (x > 548 && y > 706 && x < 637 && y < 799) {
                    bodyPart = (ImageView) findViewById(R.id.groin_right);
                }
                if (x > 438 && y > 825 && x < 530 && y < 957) {
                    bodyPart = (ImageView) findViewById(R.id.thigh_left);
                }
                if (x > 545 && y > 825 && x < 635 && y < 957) {
                    bodyPart = (ImageView) findViewById(R.id.thigh_right);
                }
                if (x > 438 && y > 969 && x < 518 && y < 1020) {
                    bodyPart = (ImageView) findViewById(R.id.knee_left);
                }
                if (x > 551 && y > 969 && x < 635 && y < 1020) {
                    bodyPart = (ImageView) findViewById(R.id.knee_right);
                }
                if (x > 438 && y > 1037 && x < 518 && y < 1220) {
                    bodyPart = (ImageView) findViewById(R.id.lower_leg_left);
                }
                if (x > 551 && y > 1030 && x < 626 && y < 1225) {
                    bodyPart = (ImageView) findViewById(R.id.lower_leg_right);
                }
                if (x > 440 && y > 1237 && x < 527 && y < 1290) {
                    bodyPart = (ImageView) findViewById(R.id.foot_left);
                }
                if (x > 542 && y > 1237 && x < 635 && y < 1290) {
                    bodyPart = (ImageView) findViewById(R.id.foot_right);
                }
                if (x > 458 && y > 453 && x < 527 && y < 584) {
                    bodyPart = (ImageView) findViewById(R.id.chest_left);
                }
                if (x > 545 && y > 456 && x < 608 && y < 584) {
                    bodyPart = (ImageView) findViewById(R.id.chest_right);
                }
                if (x > 485 && y > 411 && x < 578 && y < 438) {
                    bodyPart = (ImageView) findViewById(R.id.neck);
                }
                if (x > 470 && y > 271 && x < 596 && y < 405) {
                    bodyPart = (ImageView) findViewById(R.id.head);
                }
                if (x > 380 && y > 490 && x < 435 && y < 617) {
                    bodyPart = (ImageView) findViewById(R.id.upper_arm_left);
                }
                if (x > 360 && y > 630 && x < 420 && y < 750) {
                    bodyPart = (ImageView) findViewById(R.id.lower_arm_left);
                }
                if (x > 345 && y > 772 && x < 420 && y < 860) {
                    bodyPart = (ImageView) findViewById(R.id.hand_left);
                }
                if (x > 623 && y > 490 && x < 695 && y < 605) {
                    bodyPart = (ImageView) findViewById(R.id.upper_arm_right);
                }
                if (x > 640 && y > 620 && x < 700 && y < 750) {
                    bodyPart = (ImageView) findViewById(R.id.lower_arm_right);
                }
                if (x > 659 && y > 763 && x < 710 && y < 860) {
                    bodyPart = (ImageView) findViewById(R.id.hand_right);
                }

                return bodyPart;
            }

            public void paintBodyPart(boolean selected, ImageView bodyPart) {
                if (selected) {
                    bodyPart.setImageTintList(ColorStateList.valueOf(COLOR_YELLOW));
                } else {
                    bodyPart.setImageTintList(null);
                }
            }

        });
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
