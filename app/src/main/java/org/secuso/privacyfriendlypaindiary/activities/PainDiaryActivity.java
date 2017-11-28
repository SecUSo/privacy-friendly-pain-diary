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

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.tutorial.PrefManager;

/**
 * Class structure taken from tutorial at http://www.androidhive.info/2016/05/android-build-intro-slider-app/
 *
 * @author Karola Marky
 * @version 20161214
 */

public class PainDiaryActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnBack, btnNext, btnDone;
    private PrefManager prefManager;

    private static final String TAG = PainDiaryActivity.class.getSimpleName();
    public static final String ACTION_SHOW_ANYWAYS = TAG + ".ACTION_SHOW_ANYWAYS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        Intent i = getIntent();

        if (!prefManager.isFirstTimeLaunch() && (i == null || !ACTION_SHOW_ANYWAYS.equals(i.getAction()))) {
            launchHomeScreen();
            return;
        }

        // Making notification bar transparent
        if(Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

    setContentView(R.layout.activity_next_and_back);

    viewPager =(ViewPager) findViewById(R.id.view_pager);

    dotsLayout =(LinearLayout) findViewById(R.id.layoutDots);

    btnBack =(Button) findViewById(R.id.btn_back);

    btnBack.setVisibility(View.GONE);

    btnNext =(Button) findViewById(R.id.btn_next);

    btnDone =(Button) findViewById(R.id.btn_done);

    // layouts of all welcome sliders
    // add few more layouts if you want
    layouts =new int[]

    {
            R.layout.activity_how_do_you_feel,
            R.layout.activity_how_strong_is_your_pain,
            R.layout.activity_where_is_the_pain,
            R.layout.activity_describe_your_pain_more_closely,
            R.layout.activity_when_do_you_have_pain
    }

    ;

    // adding bottom dots
    addBottomDots(0);

    // making notification bar transparent
    changeStatusBarColor();

    myViewPagerAdapter =new

    MyViewPagerAdapter();
    viewPager.setAdapter(myViewPagerAdapter);
    viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

    btnNext.setOnClickListener(new View.OnClickListener()
    {
        @Override
        public void onClick (View v){
        // checking for last page
        // if last page home screen will be launched
        int current = getItem(+1);
        viewPager.setCurrentItem(current);

    }
    });

    btnBack.setOnClickListener(new View.OnClickListener()
    {
        @Override
        public void onClick (View v){
            // checking for last page
            // if last page home screen will be launched
            int current = getItem(-1);
            viewPager.setCurrentItem(current);

        }
    });

    btnDone.setOnClickListener(new View.OnClickListener()
    {
        @Override
        public void onClick (View v){
            // checking for last page
            // if last page home screen will be launched
            launchHomeScreen();
        }
    });

}

    public void changeRadioButtonColor(View view){
        RadioButton[] arrayOfRadioButtons = new RadioButton[]{
                (RadioButton) findViewById( R.id.image_very_satisfied),
                (RadioButton) findViewById( R.id.image_satisfied),
                (RadioButton) findViewById( R.id.image_neutral),
                (RadioButton) findViewById( R.id.image_dissatisfied),
                (RadioButton) findViewById( R.id.image_very_dissatisfied)};
        RadioButton rb = ((RadioButton) view);

        for (int i = 0 ; i < arrayOfRadioButtons.length ; i++){
            if(arrayOfRadioButtons[i].isChecked()){
                arrayOfRadioButtons[i].setBackgroundColor(Color.GREEN);
            }else{
                arrayOfRadioButtons[i].setBackgroundColor(Color.WHITE);

            }
        }

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
        prefManager.setFirstTimeLaunch(false);
        Intent intent = new Intent(PainDiaryActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == 0) {
                // last page. make button text to GOT IT
                btnNext.setVisibility(View.VISIBLE);

                btnBack.setVisibility(View.GONE);
            } else  if(position == layouts.length-1){
                // still pages are left
                btnNext.setVisibility(View.GONE);
                btnDone.setVisibility(View.VISIBLE);

            }else{
                btnNext.setVisibility(View.VISIBLE);
                btnDone.setVisibility(View.GONE);
                btnBack.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

/**
 * View pager adapter
 */
public class MyViewPagerAdapter extends PagerAdapter {
    private LayoutInflater layoutInflater;

    public MyViewPagerAdapter() {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(layouts[position], container, false);
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
}

//
//<Button
//        android:id="@+id/btn_next"
//                android:layout_width="wrap_content"
//                android:layout_height="wrap_content"
//                android:layout_alignParentBottom="true"
//                android:layout_alignParentRight="true"
//                android:layout_alignTop="@+id/btn_back"
//                android:background="@null"
//                android:drawableTop="@drawable/ic_next"
//                android:visibility="visible"
//
//                android:textColor="@android:color/black" />
//
//<Button
//        android:id="@+id/btn_back"
//                android:layout_width="wrap_content"
//                android:layout_height="35dp"
//                android:layout_alignParentBottom="true"
//                android:layout_alignParentLeft="true"
//
//                android:background="@null"
//                android:drawableTop="@drawable/ic_black"
//                android:textColor="@android:color/black" />
//
//<Button
//        android:id="@+id/btn_done"
//                android:layout_width="wrap_content"
//                android:layout_height="wrap_content"
//                android:layout_alignParentBottom="true"
//                android:layout_alignParentRight="true"
//                android:layout_alignTop="@+id/btn_back"
//                android:background="@null"
//                android:drawableTop="@drawable/ic_done"
//                android:visibility="invisible"
//                android:textColor="@android:color/black" />
