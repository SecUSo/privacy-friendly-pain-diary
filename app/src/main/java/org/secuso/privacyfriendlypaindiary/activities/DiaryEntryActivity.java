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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.BodyRegion;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Condition;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.PainQuality;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Time;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.DiaryEntry;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.Drug;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.DrugIntake;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.PainDescription;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugIntakeInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.PainDescriptionInterface;
import org.secuso.privacyfriendlypaindiary.helpers.AutocompleteAdapter;
import org.secuso.privacyfriendlypaindiary.helpers.Helper;
import org.secuso.privacyfriendlypaindiary.helpers.MyViewPagerAdapter;
import org.secuso.privacyfriendlypaindiary.helpers.RetainedFragment;
import org.secuso.privacyfriendlypaindiary.viewmodel.DatabaseViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * This activity allows to create a new diary entry or edit an existing one.
 * There currently are 7 slides on which the user can input data such as his
 * condition and the intensity, location, nature and time of the pain he feels,
 * as well the medication he is taking and additional notes.
 *
 * @author Susanne Felsen
 * @version 20180228
 *
 * <a href="https://blahti.wordpress.com/2012/06/26/images-with-clickable-areas/">Click here</a>
 * for a tutorial for making parts of images clickable.
 */
public class DiaryEntryActivity extends AppCompatActivity {

    private static final String TAG = DiaryEntryActivity.class.getSimpleName();
    private static final int COLOR_MIDDLEGREY = Color.parseColor("#a8a8a8");
    private static final int COLOR_LIGHTBLUE = Color.parseColor("#0274b2");
    private static final int COLOR_YELLOW = Color.parseColor("#f6d126");

    private static final String TAG_RETAINED_FRAGMENT = "DIARY_ENTRY_FRAGMENT";

    private RetainedFragment retainedFragment;

    private boolean changesMade = false;
    private boolean edit = false;

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
    private EnumSet<BodyRegion> bodyRegionsFront = EnumSet.noneOf(BodyRegion.class);
    private EnumSet<BodyRegion> bodyRegionsBack = EnumSet.noneOf(BodyRegion.class);
    private EnumSet<PainQuality> painQualities = EnumSet.noneOf(PainQuality.class);
    private EnumSet<Time> timesOfPain = EnumSet.noneOf(Time.class);
    private List<DrugInterface> drugs = new ArrayList<>();
    private ArrayList<DrugIntakeInterface> drugIntakes = new ArrayList<>();
    private DiaryEntryInterface diaryEntry;

    private int currentPage = 0;

    private Bitmap front;
    private Bitmap back;

    private DatabaseViewModel database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diaryentry);

        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt("current");
        }

        database = new ViewModelProvider(this).get(DatabaseViewModel.class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layout_dots);
        btnBack = findViewById(R.id.btn_back);
        btnNext = findViewById(R.id.btn_next);

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
                btnBack.setVisibility(View.VISIBLE);
                if (position == 0) {
                    btnBack.setVisibility(View.GONE);
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
                    launchHomeScreen();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(-1);
                if (current >= 0) {
                    viewPager.setCurrentItem(current);
                }
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
        dateAsString = getIntent().getStringExtra("DATE_OF_ENTRY");
        if (dateAsString == null) {
            dateAsString = dateFormat.format(Calendar.getInstance().getTime());
        }
        Date date;
        try {
            date = dateFormat.parse(dateAsString);
        } catch (ParseException e) {
            date = Calendar.getInstance().getTime();
        }
        edit = getIntent().getBooleanExtra("EDIT", false);

        // find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        retainedFragment = (RetainedFragment) fm.findFragmentByTag(TAG_RETAINED_FRAGMENT);

        if (!edit) {
            diaryEntry = new DiaryEntry(date);
            initDataFromDiaryEntry(savedInstanceState, date);
        } else {
            setTitle(getString(R.string.edit_diary_entry));
            LiveData<DiaryEntryInterface> diaryEntryLive = database.getDiaryEntryByDate(date);
            Date finalDate = date;
            diaryEntryLive.observe(this, diaryEntryInterface -> {
                diaryEntry = diaryEntryInterface;
                initDataFromDiaryEntry(savedInstanceState, finalDate);
            });
        }

    }

    private void initDataFromDiaryEntry(Bundle savedInstanceState, Date date) {
        if (diaryEntry == null) diaryEntry = new DiaryEntry(date); //this is an error case

        if (retainedFragment != null) {
            DiaryEntryInterface temp = retainedFragment.getDiaryEntry();
            initFields(temp);
        } else if (edit) {
            initFields(diaryEntry);
        } else {
            boolean rememberMedication = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.KEY_PREF_MEDICATION, true);
            if (rememberMedication) {
                LiveData<Long> IDLive = database.getIDOfLatestDiaryEntry();
                IDLive.observe(this, ID -> {
                    if (ID != -1) {
                        LiveData<Set<DrugIntakeInterface>> intakesLive = database.getDrugIntakesForDiaryEntry(ID);
                        intakesLive.observe(this, intakes -> {
                            for (DrugIntakeInterface intake : intakes) {
                                drugIntakes.add(new DrugIntake(intake));
                            }
                        });
                    }
                });
            }
        }

        if (savedInstanceState == null) {
            viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    setDataOnSlide1();
                    viewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
        LiveData<List<DrugInterface>> drugsLive = database.getAllDrugs();
        drugsLive.observe(this, drugInterfaces -> drugs = drugInterfaces);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isFinishing() && retainedFragment != null) {
            getFragmentManager().beginTransaction().remove(retainedFragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("current", viewPager.getCurrentItem());

        if (retainedFragment == null) {
            retainedFragment = new RetainedFragment();
            getFragmentManager().beginTransaction().add(retainedFragment, TAG_RETAINED_FRAGMENT).commit();
        }
        DiaryEntryInterface temp = new DiaryEntry(diaryEntry.getDate());
        setFields(temp);
        retainedFragment.setDiaryEntry(temp);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int current = savedInstanceState.getInt("current");

        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initPage(current);
                viewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    private void initPage(int position) {
        if (position == 0) {
            btnBack.setVisibility(View.GONE);
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
    }

    private void initFields(DiaryEntryInterface diaryEntry) {
        if (diaryEntry != null) {
            condition = diaryEntry.getCondition();
            notes = diaryEntry.getNotes();
            drugIntakes.addAll(diaryEntry.getDrugIntakes());
            PainDescriptionInterface painDescription = diaryEntry.getPainDescription();
            if (painDescription != null) {
                painLevel = painDescription.getPainLevel();
                EnumSet<BodyRegion> bodyRegions = painDescription.getBodyRegions();
                // body regions are split up into two separate sets (front and back)
                for (BodyRegion region : bodyRegions) {
                    if (region.getValue() < BodyRegion.LOWEST_BACK_INDEX) {
                        bodyRegionsFront.add(region);
                    } else {
                        bodyRegionsBack.add(region);
                    }
                }
                painQualities = painDescription.getPainQualities();
                timesOfPain = painDescription.getTimesOfPain();
            }
            initPage(currentPage);
        }
    }

    private void setDataOnSlide1() {
        if (findViewById(R.id.diaryentry_slide1) != null) {
            TextView date = findViewById(R.id.date);
            date.setText(dateAsString);

            initConditions();
            if (condition != null) {
                conditions[condition.getValue()].setBackgroundColor(COLOR_YELLOW);
            }
        }
    }

    private void initConditions() {
        conditions = new RadioButton[]{
                findViewById(R.id.condition_very_bad),
                findViewById(R.id.condition_bad),
                findViewById(R.id.condition_okay),
                findViewById(R.id.condition_good),
                findViewById(R.id.condition_very_good)};
    }

    private void setDataOnSlide2() {
        if (findViewById(R.id.diaryentry_slide2) != null) {
            SeekBar seekBar = findViewById(R.id.painlevel_seekbar);
//            TextView label = findViewById(R.id.label);
//            seekBar.setPaddingRelative(label.getWidth() / 2, 0, label.getWidth() / 2, 0);
            seekBar.setProgress(painLevel);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) painLevel = progress;
                    changesMade = true;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
    }

    private void setDataOnSlide3() {
        if (findViewById(R.id.diaryentry_slide3) != null) {
            initClickableBodyRegions(bodyRegionsFront, R.id.person, R.id.person_coloured, R.id.bodyregion_value, true);
            initClickableBodyRegions(bodyRegionsBack, R.id.person_back, R.id.person_back_coloured, R.id.bodyregion_back_value, false);
        }
    }

    /**
     * @param bodyRegions
     * @param personID         resource ID of ImageView displaying the person
     * @param personColouredID resource ID of (invisible) ImageView displaying the coloured person
     * @param valueID          resource ID of Image View displaying the selected body parts
     * @param isFront          indicates whether this concerns the body regions on the front of the body or on the back
     */
    private void initClickableBodyRegions(final EnumSet<BodyRegion> bodyRegions, int personID, final int personColouredID, final int valueID, final boolean isFront) {
        ImageView person = findViewById(personID);
        if (!bodyRegions.isEmpty()) {
//            Bitmap[] images = getBitmapArrayForBodyRegions(bodyRegions);
            ImageView img = findViewById(valueID);
            if (isFront) {
                this.front = Helper.overlay(this, bodyRegions);
                img.setImageBitmap(this.front);
            } else {
                this.back = Helper.overlay(this, bodyRegions);
                img.setImageBitmap(this.back);
            }
            img.setVisibility(View.VISIBLE);
        }

        if (person != null) {
            person.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        final float x = event.getX();
                        final float y = event.getY();
                        ImageView img = findViewById(personColouredID);
                        Glide.with(DiaryEntryActivity.this)
                                .asBitmap()
                                .load(R.drawable.paindiary_person_fullbody_coloured)
                                .into(new SimpleTarget<Bitmap>(img.getWidth(), img.getHeight()) {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        int touchColor = resource.getPixel(Math.round(x), Math.round(y));
                                        BodyRegion bodyPart = getBodyRegion(touchColor, isFront);

                                        if (bodyPart != null) {
                                            ImageView img = findViewById(valueID);
                                            if (!bodyRegions.contains(bodyPart)) {
                                                bodyRegions.add(bodyPart);
                                                changesMade = true;

//                                            Bitmap[] images = getBitmapArrayForBodyRegions(bodyRegions);
//                                            ((ImageView) findViewById(valueID)).setImageBitmap(Helper.overlay(images));
                                                Bitmap bitmapToAdd = BitmapFactory.decodeResource(getResources(), bodyPart.getResourceID());
                                                Bitmap value;
                                                if (isFront) {
                                                    if (getFront() == null) {
                                                        value = bitmapToAdd;
                                                    } else {
                                                        value = Helper.overlay(getFront(), bitmapToAdd);
                                                    }
                                                    setFront(value);
                                                } else {
                                                    if (getBack() == null) {
                                                        value = bitmapToAdd;
                                                    } else {
                                                        value = Helper.overlay(getBack(), bitmapToAdd);
                                                    }
                                                    setBack(value);
                                                }
                                                img.setImageBitmap(value);
                                                img.setVisibility(View.VISIBLE);
                                            } else { //already selected >> deselect
                                                bodyRegions.remove(bodyPart);
                                                changesMade = true;

//                                            if (!bodyRegions.isEmpty()) {
//                                                Bitmap[] images = getBitmapArrayForBodyRegions(bodyRegions);
//                                                ((ImageView) findViewById(valueID)).setImageBitmap(Helper.overlay(images));
//                                                findViewById(valueID).setVisibility(View.VISIBLE);
//                                            } else {
//                                                findViewById(valueID).setVisibility(View.GONE);
//                                            }
                                                if (isFront) {
                                                    if (bodyRegions.isEmpty() || getFront() == null) {
                                                        setFront(null);
                                                        img.setVisibility(View.GONE);
                                                    } else {
//                                                    Bitmap[] images = getBitmapArrayForBodyRegions(bodyRegions);
                                                        Bitmap value = Helper.overlay(DiaryEntryActivity.this, bodyRegions);
                                                        setFront(value);
                                                        img.setImageBitmap(value);
                                                        img.setVisibility(View.VISIBLE);
                                                    }
                                                } else {
                                                    if (bodyRegions.isEmpty() || getBack() == null) {
                                                        setBack(null);
                                                        img.setVisibility(View.GONE);
                                                    } else {
//                                                    Bitmap[] images = getBitmapArrayForBodyRegions(bodyRegions);
                                                        Bitmap value = Helper.overlay(DiaryEntryActivity.this, bodyRegions);
                                                        setBack(value);
                                                        img.setImageBitmap(value);
                                                        img.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                    return false;
                }
            });
        }
    }

    private Bitmap getFront() {
        return front;
    }

    private void setFront(Bitmap front) {
        this.front = front;
    }

    private Bitmap getBack() {
        return back;
    }

    private void setBack(Bitmap back) {
        this.back = back;
    }

    private Bitmap[] getBitmapArrayForBodyRegions(EnumSet<BodyRegion> bodyRegions) {
        Bitmap[] images = new Bitmap[bodyRegions.size()];
        int i = 0;
        for (BodyRegion region : bodyRegions) {
            images[i] = BitmapFactory.decodeResource(getResources(), region.getResourceID());
            i++;
        }
        return images;
    }

    private void setDataOnSlide4() {
        if (findViewById(R.id.diaryentry_slide4) != null) {
            if (painQualities.contains(PainQuality.STABBING)) {
                ((CheckBox) findViewById(R.id.pain_stabbing)).setChecked(true);
            }
            if (painQualities.contains(PainQuality.DULL)) {
                ((CheckBox) findViewById(R.id.pain_dull)).setChecked(true);
            }
            if (painQualities.contains(PainQuality.SHOOTING)) {
                ((CheckBox) findViewById(R.id.pain_shooting)).setChecked(true);
            }
            if (painQualities.contains(PainQuality.BURNING)) {
                ((CheckBox) findViewById(R.id.pain_burning)).setChecked(true);
            }
            if (painQualities.contains(PainQuality.THROBBING)) {
                ((CheckBox) findViewById(R.id.pain_throbbing)).setChecked(true);
            }
        }
    }

    private void setDataOnSlide5() {
        if (findViewById(R.id.diaryentry_slide5) != null) {
            if (timesOfPain.contains(Time.ALL_DAY)) {
                ((CheckBox) findViewById(R.id.time_all_day)).setChecked(true);
            }
            if (timesOfPain.contains(Time.MORNING)) {
                ((CheckBox) findViewById(R.id.time_morning)).setChecked(true);
            }
            if (timesOfPain.contains(Time.AFTERNOON)) {
                ((CheckBox) findViewById(R.id.time_afternoon)).setChecked(true);
            }
            if (timesOfPain.contains(Time.EVENING)) {
                ((CheckBox) findViewById(R.id.time_evening)).setChecked(true);
            }
            if (timesOfPain.contains(Time.NIGHT)) {
                ((CheckBox) findViewById(R.id.time_night)).setChecked(true);
            }
        }
    }

    private void setDataOnSlide6() {
        if (findViewById(R.id.diaryentry_slide6) != null) {
            LinearLayout layout = findViewById(R.id.medication_container);
            layout.removeAllViews();
            for (DrugIntakeInterface drugIntake : drugIntakes) {
                layout.addView(initMedicationView(layout, drugIntake), layout.getChildCount());
            }
        }
    }

    private void setDataOnSlide7() {
        if (findViewById(R.id.diaryentry_slide7) != null) {
            final EditText notesEditText = findViewById(R.id.notes_text);
            notesEditText.setText(notes);
            notesEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence text, int start, int count, int after) {
                    String result = text.toString().replaceAll("\n", " ");
                    if (!text.toString().equals(result)) {
                        notesEditText.setText(result);
                        notesEditText.setSelection(result.length());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    notes = notesEditText.getText().toString();
                    changesMade = true;
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
            });
        }
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(COLOR_MIDDLEGREY);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(COLOR_LIGHTBLUE);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        Intent intent = new Intent(DiaryEntryActivity.this, MainActivity.class);
        intent.putExtra("DATE_TO_DISPLAY", dateAsString);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void selectCondition(View view) {
        changesMade = true;
        if (conditions == null) initConditions();
        for (int i = 0; i < conditions.length; i++) {
            if (conditions[i].isChecked()) {
                //condition will be deselected if it is already selected
                if (condition == Condition.valueOf(i)) {
                    conditions[i].setBackgroundColor(Color.TRANSPARENT);
                    ((RadioGroup) findViewById(R.id.condition_group)).clearCheck();
                    condition = null;
                } else {
                    conditions[i].setBackgroundColor(COLOR_YELLOW);
                    condition = Condition.valueOf(i);
                }
            } else {
                conditions[i].setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    public void onPainQualityClicked(View view) {
        changesMade = true;
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
            case R.id.pain_burning:
                quality = PainQuality.BURNING;
                break;
            case R.id.pain_throbbing:
                quality = PainQuality.THROBBING;
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
        changesMade = true;
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
            case R.id.time_night:
                time = Time.NIGHT;
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

    private boolean closeMatch(int color1, int color2, int tolerance) {
        if (Math.abs(Color.red(color1) - Color.red(color2)) > tolerance)
            return false;
        if (Math.abs(Color.green(color1) - Color.green(color2)) > tolerance)
            return false;
        if (Math.abs(Color.blue(color1) - Color.blue(color2)) > tolerance)
            return false;
        return true;
    }

    private BodyRegion getBodyRegion(int touchColor, boolean front) {
        int tolerance = 25;
        BodyRegion bodyPart = null;
        if (closeMatch(Color.parseColor("#0000ff"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.ABDOMEN_LEFT;
            } else {
                bodyPart = BodyRegion.ABDOMEN_LEFT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#ff00ff"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.ABDOMEN_RIGHT;
            } else {
                bodyPart = BodyRegion.ABDOMEN_RIGHT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#ffff00"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.GROIN_LEFT;
            } else {
                bodyPart = BodyRegion.GROIN_LEFT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#00ff00"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.GROIN_RIGHT;
            } else {
                bodyPart = BodyRegion.GROIN_RIGHT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#ff7e00"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.THIGH_LEFT;
            } else {
                bodyPart = BodyRegion.THIGH_LEFT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#a774d2"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.THIGH_RIGHT;
            } else {
                bodyPart = BodyRegion.THIGH_RIGHT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#147914"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.KNEE_LEFT;
            } else {
                bodyPart = BodyRegion.KNEE_LEFT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#775205"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.KNEE_RIGHT;
            } else {
                bodyPart = BodyRegion.KNEE_RIGHT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#ff007e"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.LOWER_LEG_LEFT;
            } else {
                bodyPart = BodyRegion.LOWER_LEG_LEFT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#00ffff"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.LOWER_LEG_RIGHT;
            } else {
                bodyPart = BodyRegion.LOWER_LEG_RIGHT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#7ec8ff"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.FOOT_LEFT;
            } else {
                bodyPart = BodyRegion.FOOT_LEFT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#173081"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.FOOT_RIGHT;
            } else {
                bodyPart = BodyRegion.FOOT_RIGHT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#007ba9"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.CHEST_LEFT;
            } else {
                bodyPart = BodyRegion.CHEST_LEFT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#00ffb4"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.CHEST_RIGHT;
            } else {
                bodyPart = BodyRegion.CHEST_RIGHT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#042c3a"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.NECK;
            } else {
                bodyPart = BodyRegion.NECK_BACK;
            }
        } else if (closeMatch(Color.parseColor("#ff0000"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.HEAD;
            } else {
                bodyPart = BodyRegion.HEAD_BACK;
            }
        } else if (closeMatch(Color.parseColor("#81173d"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.UPPER_ARM_LEFT;
            } else {
                bodyPart = BodyRegion.UPPER_ARM_LEFT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#bc3b13"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.UPPER_ARM_RIGHT;
            } else {
                bodyPart = BodyRegion.UPPER_ARM_RIGHT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#7e007e"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.LOWER_ARM_LEFT;
            } else {
                bodyPart = BodyRegion.LOWER_ARM_LEFT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#7e7e00"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.LOWER_ARM_RIGHT;
            } else {
                bodyPart = BodyRegion.LOWER_ARM_RIGHT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#7e7e7e"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.HAND_LEFT;
            } else {
                bodyPart = BodyRegion.HAND_LEFT_BACK;
            }
        } else if (closeMatch(Color.parseColor("#7e7eff"), touchColor, tolerance)) {
            if (front) {
                bodyPart = BodyRegion.HAND_RIGHT;
            } else {
                bodyPart = BodyRegion.HAND_RIGHT_BACK;
            }
        }
        return bodyPart;
    }

    public void addMedication(View view) {
        final LinearLayout layout = findViewById(R.id.medication_container);
        DrugIntakeInterface drugIntake = new DrugIntake(new Drug(null, null), 0, 0, 0, 0);
        layout.addView(initMedicationView(layout, drugIntake), layout.getChildCount());
        drugIntakes.add(drugIntake);
    }

    private View initMedicationView(final LinearLayout parent, final DrugIntakeInterface drugIntake) {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View newView = inflater.inflate(R.layout.component_medication, null);
        ImageButton remove = newView.findViewById(R.id.remove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.removeView(newView);
                drugIntakes.remove(drugIntake);
                if (edit && drugIntake.isPersistent()) {
                    diaryEntry.removeDrugIntake(diaryEntry.getDrugIntakeByID(drugIntake.getObjectID()));
                }
            }
        });
        final EditText doseEditText = newView.findViewById(R.id.dose);
        doseEditText.setText(drugIntake.getDrug().getDose());
        doseEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String dose = doseEditText.getText().toString().trim();
                if (dose.isEmpty()) {
                    dose = null;
                }
                drugIntake.getDrug().setDose(dose);
                changesMade = true;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        final AutoCompleteTextView nameEditText = newView.findViewById(R.id.medication_name);
        nameEditText.setText(drugIntake.getDrug().getName());
        //android.R.layout.simple_dropdown_item_1line
        final AutocompleteAdapter adapter = new AutocompleteAdapter(this, android.R.layout.simple_list_item_1, drugs);
        nameEditText.setAdapter(adapter);
        nameEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setItemClicked(true);
                nameEditText.setText(nameEditText.getText());
            }
        });
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = nameEditText.getText().toString().trim();
                if (name.isEmpty()) {
                    name = null;
                }
                drugIntake.getDrug().setName(name);
                changesMade = true;

                if (adapter.isItemClicked()) {
                    String dose = adapter.getDose();
                    if (dose != null) {
                        doseEditText.setText(dose);
                        doseEditText.refreshDrawableState();
                    }
                }
                adapter.setItemClicked(false);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        final EditText quantityMorningEditText = newView.findViewById(R.id.quantity_morning);
        quantityMorningEditText.setText(String.valueOf(drugIntake.getQuantityMorning()));
        quantityMorningEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String quantity = quantityMorningEditText.getText().toString().trim();
                if (!quantity.isEmpty()) {
                    try {
                        int i = Integer.parseInt(quantity);
                        drugIntake.setQuantityMorning(i);
                        changesMade = true;
                    } catch (NumberFormatException e) {

                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        final EditText quantityNoonEditText = newView.findViewById(R.id.quantity_noon);
        quantityNoonEditText.setText(String.valueOf(drugIntake.getQuantityNoon()));
        quantityNoonEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String quantity = quantityNoonEditText.getText().toString().trim();
                if (!quantity.isEmpty()) {
                    try {
                        int i = Integer.parseInt(quantity);
                        drugIntake.setQuantityNoon(i);
                        changesMade = true;
                    } catch (NumberFormatException e) {

                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        final EditText quantityEveningEditText = newView.findViewById(R.id.quantity_evening);
        quantityEveningEditText.setText(String.valueOf(drugIntake.getQuantityEvening()));
        quantityEveningEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String quantity = quantityEveningEditText.getText().toString().trim();
                if (!quantity.isEmpty()) {
                    try {
                        int i = Integer.parseInt(quantity);
                        drugIntake.setQuantityEvening(i);
                        changesMade = true;
                    } catch (NumberFormatException e) {

                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        final EditText quantityNightEditText = newView.findViewById(R.id.quantity_night);
        quantityNightEditText.setText(String.valueOf(drugIntake.getQuantityNight()));
        quantityNightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String quantity = quantityNightEditText.getText().toString().trim();
                if (!quantity.isEmpty()) {
                    try {
                        int i = Integer.parseInt(quantity);
                        drugIntake.setQuantityNight(i);
                        changesMade = true;
                    } catch (NumberFormatException e) {

                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
        return newView;
    }

    private void setFields(DiaryEntryInterface diaryEntry) {
        diaryEntry.setCondition(condition);
        diaryEntry.setNotes(notes);

        for (DrugIntakeInterface drugIntake : drugIntakes) {
            if (!drugIntake.isPersistent() && drugIntake.getDrug().getName() != null) {
                diaryEntry.addDrugIntake(drugIntake);
            }
        }

        EnumSet<BodyRegion> bodyRegions = EnumSet.noneOf(BodyRegion.class);
        bodyRegions.addAll(bodyRegionsFront);
        bodyRegions.addAll(bodyRegionsBack);
        if (edit && diaryEntry.getPainDescription() != null) {
            PainDescriptionInterface painDescription = diaryEntry.getPainDescription();
            painDescription.setPainLevel(painLevel);
            painDescription.setBodyRegions(bodyRegions);
            painDescription.setPainQualities(painQualities);
            painDescription.setTimesOfPain(timesOfPain);
        } else {
            PainDescriptionInterface painDescription = new PainDescription(painLevel, bodyRegions, painQualities, timesOfPain);
            diaryEntry.setPainDescription(painDescription);
        }
    }

    public void save() {
        setFields(diaryEntry);
        if (!edit) {
            database.storeDiaryEntryAndAssociatedObjects(diaryEntry);
        } else {
            database.updateDiaryEntryAndAssociatedObjects(diaryEntry);
        }
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
                save();
                launchHomeScreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (changesMade) {
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

        } else {
            finish();
        }
    }

}
