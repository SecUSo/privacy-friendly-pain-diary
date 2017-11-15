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

package org.secuso.privacyfriendlyexample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.secuso.privacyfriendlyexample.R;
import org.secuso.privacyfriendlyexample.tutorial.PrefManager;

/**
 * Created by yonjuni on 15.06.16.
 */


public class wieFuelenSieSich extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wie_fuehlen_sie_sich);

        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(BaseActivity.MAIN_CONTENT_FADEIN_DURATION);
        }


        // Add a new diary Entry
        Button add = (Button) findViewById(R.id.button_next);
        if(add != null) {
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PrefManager prefManager = new PrefManager(getBaseContext());
                    prefManager.setFirstTimeLaunch(true);
                    Intent intent = new Intent(wieFuelenSieSich.this, WieStarkSindDieSchmerzen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }

        overridePendingTransition(0, 0);

//        ((TextView)findViewById(R.id.secusoWebsite)).setMovementMethod(LinkMovementMethod.getInstance());
//        ((TextView)findViewById(R.id.githubURL)).setMovementMethod(LinkMovementMethod.getInstance());
//        ((TextView)findViewById(R.id.textFieldVersionName)).setText(BuildConfig.VERSION_NAME);
    }

//@Override
//protected int getNavigationDrawerID() {
//    return R.id.nav_about;
//}

}