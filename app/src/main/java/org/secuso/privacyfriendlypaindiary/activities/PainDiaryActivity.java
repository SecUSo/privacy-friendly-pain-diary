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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.secuso.privacyfriendlypaindiary.R;

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

    private static final String TAG = PainDiaryActivity.class.getSimpleName();
    public static final String ACTION_SHOW_ANYWAYS = TAG + ".ACTION_SHOW_ANYWAYS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        if(btnDone==null){
            Toast.makeText(getApplicationContext(), "null",
                    Toast.LENGTH_LONG).show();
        }
        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts =new int[]

        {
                R.layout.activity_how_do_you_feel,
                R.layout.activity_how_strong_is_your_pain,
                R.layout.activity_where_is_the_pain,
                R.layout.activity_describe_your_pain_more_closely,
                R.layout.activity_when_do_you_have_pain,
                R.layout.activity_do_you_hava_any_remarks,
                R.layout.activity_what_medications_do_you_take
        }

        ;

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter =new MyViewPagerAdapter();

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



    public void changeColorOfBodyParts(View v){

        ImageView kopf =  (ImageView)findViewById(R.id.Kopf);
        kopf.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    float x = event.getX();
                    float y = event.getY();

                    ImageView bodyPart = getKlickedBodyPart(x,y);

                    if(bodyPart!=null){
                        if(bodyPart.getImageTintList()==null){
                            paintBodyPart(1,bodyPart);
                        }else{
                            paintBodyPart(0,bodyPart);
                        }
                    }

                }
                return false;
            }

            public ImageView getKlickedBodyPart(float x,float y){
                ImageView bodyPart=null;
                if(x>545 && y>596 && x<614 && y<688 ) {
                    bodyPart = (ImageView) findViewById(R.id.BauchRechts);
                }
                if(x>458 && y>596 && x<521 && y<688 ) {
                    bodyPart = (ImageView) findViewById(R.id.BauchLinks);
                }
                if(x>435 && y>703 && x<518 && y<799 ) {
                    bodyPart = (ImageView) findViewById(R.id.LeisteLinks);
                }
                if(x>548 && y>706 && x<637 && y<799 ) {
                    bodyPart = (ImageView) findViewById(R.id.LeisteRechts);
                }
                if(x>438 && y>825 && x<530 && y<957 ) {
                    bodyPart = (ImageView) findViewById(R.id.OberschenkelLinks);
                }
                if(x>545 && y>825 && x<635 && y<957 ) {
                    bodyPart = (ImageView) findViewById(R.id.OberschenkelRechts);
                }
                if(x>438 && y>969 && x<518 && y<1020 ) {
                    bodyPart = (ImageView) findViewById(R.id.KnieLinks);
                }
                if(x>551 && y>969 && x<635 && y<1020 ) {
                    bodyPart = (ImageView) findViewById(R.id.KnieRechts);
                }
                if(x>438 && y>1037 && x<518 && y<1220 ) {
                    bodyPart = (ImageView) findViewById(R.id.UnterschenkelLinks);
                }
                if(x>551 && y>1030 && x<626 && y<1225 ) {
                    bodyPart = (ImageView) findViewById(R.id.UnterschenkelRechts);
                }
                if(x>440 && y>1237 && x<527 && y<1290 ) {
                    bodyPart = (ImageView) findViewById(R.id.FussLinks);
                }
                if(x>542 && y>1237 && x<635 && y<1290 ) {
                    bodyPart = (ImageView) findViewById(R.id.FussRechts);
                }
                if(x>458 && y>453 && x<527 && y<584 ) {
                    bodyPart = (ImageView) findViewById(R.id.BrustLinks);
                }
                if(x>545 && y>456 && x<608 && y<584 ) {
                    bodyPart = (ImageView) findViewById(R.id.BrustRechts);
                }
                if(x>485 && y>411 && x<578 && y<438 ) {
                    bodyPart = (ImageView) findViewById(R.id.Hals);
                }
                if(x>470 && y>271 && x<596 && y<405 ) {
                    bodyPart = (ImageView) findViewById(R.id.Kopf);
                }
                if(x>380 && y>490 && x<435 && y<617 ) {
                    bodyPart = (ImageView) findViewById(R.id.OberarmLinks);
                }
                if(x>360 && y>630 && x<420 && y<750 ) {
                    bodyPart = (ImageView) findViewById(R.id.UnterarmLinks);
                }
                if(x>345 && y>772 && x<420 && y<860 ) {
                    bodyPart = (ImageView) findViewById(R.id.HandLinks);
                }
                if(x>623 && y>490 && x<695 && y<605 ) {
                    bodyPart = (ImageView) findViewById(R.id.OberarmRechts);
                }
                if(x>640 && y>620 && x<700 && y<750 ) {
                    bodyPart = (ImageView) findViewById(R.id.UnterarmRechts);
                }
                if(x>659 && y>763 && x<710 && y<860 ) {
                    bodyPart = (ImageView) findViewById(R.id.HandRechts);
                }

                return bodyPart;
            }

            public void paintBodyPart(int stufe, ImageView bodyPart){
                if(stufe==1){
                    bodyPart.setImageTintList(ColorStateList.valueOf(Color.YELLOW));
                }else if(stufe==0){
                    bodyPart.setImageTintList (null);
                }
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

            if (position == 0) {

                btnNext.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.GONE);

            } else  if(position == layouts.length-1){

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
