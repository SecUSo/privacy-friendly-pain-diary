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
package org.secuso.privacyfriendlypaindiary.tutorial;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.activities.MainActivity;
import org.secuso.privacyfriendlypaindiary.activities.UserDetailsActivity;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.DBServiceInterface;
import org.secuso.privacyfriendlypaindiary.helpers.MyViewPagerAdapter;

/**
 * Class structure taken from tutorial at http://www.androidhive.info/2016/05/android-build-intro-slider-app/
 *
 * @author Karola Marky, Susanne Felsen
 * @version 20171129
 */

public class TutorialActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private PrefManager prefManager;

    private static final String TAG = TutorialActivity.class.getSimpleName();
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
        } else {
            //initialize database on first time launch
            DBServiceInterface service = DBService.getInstance(this);
            service.initializeDatabase();
        }

        // Making notification bar transparent
        if(Build.VERSION.SDK_INT >=21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_tutorial);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layout_dots);
        btnSkip = (Button) findViewById(R.id.btn_back);
        btnNext = (Button) findViewById(R.id.btn_next);

        // layouts of all welcome sliders
        layouts = new int[] {
                R.layout.tutorial_slide1,
                R.layout.tutorial_slide2,
                R.layout.tutorial_slide3,
                R.layout.tutorial_slide4,};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter(this, layouts);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
        launchHomeScreen();
    }
    });
        btnNext.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
            // checking for last page; if last page user detail screen will be launched
            int current = getItem(+1);
            if (current < layouts.length) {
                // move to next screen
                viewPager.setCurrentItem(current);
            } else {
                launchUserDetails();
//                if(prefManager.isFirstTimeLaunch()) {
//                    launchUserDetails();
//                } else {
//                    launchHomeScreen();
//                }
            }
        }
        });
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

    private void launchUserDetails() {
        prefManager.setFirstTimeLaunch(false);
        Intent intent = new Intent(TutorialActivity.this, UserDetailsActivity.class);
        intent.putExtra("TUTORIAL", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        Intent intent = new Intent(TutorialActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'OKAY'
            if (position == layouts.length - 1) {
                // last page. make button text to OKAY
                btnNext.setText(getString(R.string.okay));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
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

}
