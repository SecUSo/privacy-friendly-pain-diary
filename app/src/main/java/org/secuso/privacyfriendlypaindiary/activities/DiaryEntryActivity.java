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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
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

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.DBServiceInterface;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Tutorial for making parts of images clickable: <a href="https://blahti.wordpress.com/2012/06/26/images-with-clickable-areas/"/a>.
 *
 * @author Susanne Felsen
 * @version 20180110
 */
public class DiaryEntryActivity extends AppCompatActivity {

    private static final String TAG = DiaryEntryActivity.class.getSimpleName();
    private static final int COLOR_MIDDLEGREY = Color.parseColor("#a8a8a8");
    private static final int COLOR_LIGHTBLUE = Color.parseColor("#0274b2");
    //    private static final int COLOR_MIDDLEBLUE = Color.parseColor("#8aa5ce");
    private static final int COLOR_YELLOW = Color.parseColor("#f6d126");

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
    private EnumSet<BodyRegion> bodyRegions = EnumSet.noneOf(BodyRegion.class);
    private EnumSet<PainQuality> painQualities = EnumSet.noneOf(PainQuality.class);
    private EnumSet<Time> timesOfPain = EnumSet.noneOf(Time.class);
    private List<DrugInterface> drugs = new ArrayList<>();
    private ArrayList<DrugIntakeInterface> drugIntakes = new ArrayList<>();
    private DiaryEntryInterface diaryEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diaryentry);

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
                if (position == 0) {
                    btnBack.setVisibility(View.GONE);
                    setDataOnSlide1();
                } else if (position == 1) {
                    btnBack.setVisibility(View.VISIBLE);
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
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
        DBServiceInterface service = DBService.getInstance(this);
        if (!edit) {
            diaryEntry = new DiaryEntry(date);
        } else {
            setTitle(getString(R.string.edit_diary_entry));
            diaryEntry = service.getDiaryEntryByDate(date);
            initFields();
        }
        if (diaryEntry == null) diaryEntry = new DiaryEntry(date); //this is an error case

        if(!edit) { //TODO check settings
            long ID = service.getIDOfLatestDiaryEntry();
            if (ID != -1) {
                Set<DrugIntakeInterface> intakes = service.getDrugIntakesForDiaryEntry(ID);
                for(DrugIntakeInterface intake : intakes) {
                    drugIntakes.add(new DrugIntake(intake));
                }
            }
        }
        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setDataOnSlide1();
                viewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        drugs = service.getAllDrugs();
    }

    private void initFields() {
        if(diaryEntry != null) {
            condition = diaryEntry.getCondition();
            notes = diaryEntry.getNotes();
            drugIntakes.addAll(diaryEntry.getDrugIntakes());
            PainDescriptionInterface painDescription = diaryEntry.getPainDescription();
            if(painDescription != null) {
                painLevel = painDescription.getPainLevel();
                bodyRegions = painDescription.getBodyRegions();
                painQualities = painDescription.getPainQualities();
                timesOfPain = painDescription.getTimesOfPain();
            }
        }
    }

    private void setDataOnSlide1() {
        ((TextView) findViewById(R.id.date)).setText(dateAsString);

        initConditions();
        if (condition != null) {
            conditions[condition.getValue()].setBackgroundColor(COLOR_YELLOW);
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
        SeekBar seekBar = findViewById(R.id.painlevel_seekbar);
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

    //TODO: have a look at https://developer.android.com/training/gestures/detector.html
    private void setDataOnSlide3() {
        ImageView person = findViewById(R.id.person);
        if(!bodyRegions.isEmpty()) {
            Bitmap[] images = getBitmapArrayForBodyRegions(bodyRegions);
            ((ImageView) findViewById(R.id.bodyregion_value)).setImageBitmap(Helper.overlay(images));
            findViewById(R.id.bodyregion_value).setVisibility(View.VISIBLE);
        }
        person.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX();
                    float y = event.getY();
                    int touchColor = getHotspotColor(R.id.person_coloured, Math.round(x), Math.round(y));
                    BodyRegion bodyPart = getBodyRegion(touchColor);
                    if(bodyPart != null) {
                        if(!bodyRegions.contains(bodyPart)) {
                            bodyRegions.add(bodyPart);
                            changesMade = true;

                            Bitmap[] images = getBitmapArrayForBodyRegions(bodyRegions);
                            ((ImageView) findViewById(R.id.bodyregion_value)).setImageBitmap(Helper.overlay(images));
                            findViewById(R.id.bodyregion_value).setVisibility(View.VISIBLE);
                        } else { //already selected >> deselect
                            bodyRegions.remove(bodyPart);
                            changesMade = true;

                            if(!bodyRegions.isEmpty()) {
                                Bitmap[] images = getBitmapArrayForBodyRegions(bodyRegions);
                                ((ImageView) findViewById(R.id.bodyregion_value)).setImageBitmap(Helper.overlay(images));
                                findViewById(R.id.bodyregion_value).setVisibility(View.VISIBLE);
                            } else {
                                findViewById(R.id.bodyregion_value).setVisibility(View.GONE);
                            }
                        }
                    }

                }
                return false;
            }
        });
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

    private void setDataOnSlide4() {
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

    private void setDataOnSlide5() {
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

    private void setDataOnSlide6() {
        LinearLayout layout = findViewById(R.id.medication_container);
        layout.removeAllViews();
        for(DrugIntakeInterface drugIntake : drugIntakes) {
            layout.addView(initMedicationView(layout, drugIntake), layout.getChildCount());
        }
    }

    private void setDataOnSlide7() {
        final EditText notesEditText = findViewById(R.id.notes_text);
        notesEditText.setText(notes);
        notesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {

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
                if(condition == Condition.valueOf(i)) {
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

    private int getHotspotColor(int hotspotId, int x, int y) {
        ImageView img = findViewById(hotspotId);
        img.setDrawingCacheEnabled(true);
        if(img.getDrawingCache() == null) {
            img.setDrawingCacheEnabled(false);
            Log.d(TAG, "DRAWING CACHE ERROR");
            //TODO:
            return 0;
        } else {
            Bitmap hotspots = Bitmap.createBitmap(img.getDrawingCache());
            img.setDrawingCacheEnabled(false);
            return hotspots.getPixel(x, y);
        }
    }

    public boolean closeMatch (int color1, int color2, int tolerance) {
        if (Math.abs (Color.red (color1) - Color.red (color2)) > tolerance )
            return false;
        if (Math.abs (Color.green (color1) - Color.green (color2)) > tolerance )
            return false;
        if (Math.abs (Color.blue (color1) - Color.blue (color2)) > tolerance )
            return false;
        return true;
    }

    private BodyRegion getBodyRegion(int touchColor) {
        int tolerance = 25;
        BodyRegion bodyPart = null;
        if (closeMatch (Color.parseColor("#ff00ff"), touchColor, tolerance)) {
            bodyPart = BodyRegion.ABDOMEN_RIGHT;
        } else if (closeMatch (Color.parseColor("#0000ff"), touchColor, tolerance)) {
            bodyPart = BodyRegion.ABDOMEN_LEFT;
        } else if (closeMatch (Color.parseColor("#ffff00"), touchColor, tolerance)) {
            bodyPart = BodyRegion.GROIN_LEFT;
        } else if (closeMatch (Color.parseColor("#00ff00"), touchColor, tolerance)) {
            bodyPart = BodyRegion.GROIN_RIGHT;
        } else if (closeMatch (Color.parseColor("#ff7e00"), touchColor, tolerance)) {
            bodyPart = BodyRegion.THIGH_LEFT;
        } else if (closeMatch (Color.parseColor("#a774d2"), touchColor, tolerance)) {
            bodyPart = BodyRegion.THIGH_RIGHT;
        } else if (closeMatch (Color.parseColor("#147914"), touchColor, tolerance)) {
            bodyPart = BodyRegion.KNEE_LEFT;
        } else if (closeMatch (Color.parseColor("#775205"), touchColor, tolerance)) {
            bodyPart = BodyRegion.KNEE_RIGHT;
        } else if (closeMatch (Color.parseColor("#ff007e"), touchColor, tolerance)) {
            bodyPart = BodyRegion.LOWER_LEG_LEFT;
        } else if (closeMatch (Color.parseColor("#00ffff"), touchColor, tolerance)) {
            bodyPart = BodyRegion.LOWER_LEG_RIGHT;
        } else if (closeMatch (Color.parseColor("#7ec8ff"), touchColor, tolerance)) {
            bodyPart = BodyRegion.FOOT_LEFT;
        } else if (closeMatch (Color.parseColor("#173081"), touchColor, tolerance)) {
            bodyPart = BodyRegion.FOOT_RIGHT;
        } else if (closeMatch (Color.parseColor("#007ba9"), touchColor, tolerance)) {
            bodyPart = BodyRegion.CHEST_LEFT;
        } else if (closeMatch (Color.parseColor("#00ffb4"), touchColor, tolerance)) {
            bodyPart = BodyRegion.CHEST_RIGHT;
        } else if (closeMatch (Color.parseColor("#042c3a"), touchColor, tolerance)) {
            bodyPart = BodyRegion.NECK;
        } else if (closeMatch (Color.parseColor("#ff0000"), touchColor, tolerance)) {
            bodyPart = BodyRegion.HEAD;
        } else if (closeMatch (Color.parseColor("#81173d"), touchColor, tolerance)) {
            bodyPart = BodyRegion.UPPER_ARM_LEFT;
        } else if (closeMatch (Color.parseColor("#bc3b13"), touchColor, tolerance)) {
            bodyPart = BodyRegion.UPPER_ARM_RIGHT;
        } else if (closeMatch (Color.parseColor("#7e007e"), touchColor, tolerance)) {
            bodyPart = BodyRegion.LOWER_ARM_LEFT;
        } else if (closeMatch (Color.parseColor("#7e7e00"), touchColor, tolerance)) {
            bodyPart = BodyRegion.LOWER_ARM_RIGHT;
        } else if (closeMatch (Color.parseColor("#7e7e7e"), touchColor, tolerance)) {
            bodyPart = BodyRegion.HAND_LEFT;
        } else if (closeMatch (Color.parseColor("#7e7eff"), touchColor, tolerance)) {
            bodyPart = BodyRegion.HAND_RIGHT;
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
                if(edit && drugIntake.isPersistent()) {
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
                if(dose.isEmpty()) {
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
                if(name.isEmpty()) {
                    name = null;
                }
                drugIntake.getDrug().setName(name);
                changesMade = true;

                if(adapter.isItemClicked()) {
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
                if(!quantity.isEmpty()) {
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
                if(!quantity.isEmpty()) {
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
                if(!quantity.isEmpty()) {
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
                if(!quantity.isEmpty()) {
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

    public void save() {
        diaryEntry.setCondition(condition);
        diaryEntry.setNotes(notes);

        for(DrugIntakeInterface drugIntake : drugIntakes) {
            if(!drugIntake.isPersistent() && drugIntake.getDrug().getName() != null) {
                diaryEntry.addDrugIntake(drugIntake);
            }
        }

        if(edit && diaryEntry.getPainDescription() != null) {
            PainDescriptionInterface painDescription = diaryEntry.getPainDescription();
            painDescription.setPainLevel(painLevel);
            painDescription.setBodyRegions(bodyRegions);
            painDescription.setPainQualities(painQualities);
            painDescription.setTimesOfPain(timesOfPain);
        } else {
            PainDescriptionInterface painDescription = new PainDescription(painLevel, bodyRegions, painQualities, timesOfPain);
            diaryEntry.setPainDescription(painDescription);
        }
        DBServiceInterface service = DBService.getInstance(this);
        if(!edit) {
            service.storeDiaryEntryAndAssociatedObjects(diaryEntry);
        } else {
            service.updateDiaryEntryAndAssociatedObjects(diaryEntry);
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
                Log.d(TAG, "Diary entry saved.");
                save();
                launchHomeScreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        if(changesMade) {
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
