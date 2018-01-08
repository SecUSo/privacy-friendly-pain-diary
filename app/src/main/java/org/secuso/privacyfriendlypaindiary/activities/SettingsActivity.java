package org.secuso.privacyfriendlypaindiary.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.DBServiceInterface;

/**
 * Inspiration from: <a href="https://developer.android.com/guide/topics/ui/settings.html"/> and <a href="https://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-su/4325239#4325239"/>
 *
 * @author Susanne Felsen
 * @version 20180108
 */
public class SettingsActivity extends BaseActivity {

    public static final String KEY_PREF_RESET = "pref_reset";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        overridePendingTransition(0, 0);
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_settings;
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
//            PreferenceManager.setDefaultValues(Preferences.this, R.xml.pref_general, false);
//            initSummary(getPreferenceScreen());

            Preference resetPref = findPreference(KEY_PREF_RESET);
            if(resetPref != null) {
                resetPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference preference) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(getString(R.string.pref_reset_warning))
                                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        resetApp();
                                    }

                                })
                                .setNegativeButton(getString(R.string.cancel), null)
                                .show();
                        return true;
                    }
                });
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

//        private void initSummary(Preference p) {
//            if (p instanceof PreferenceGroup) {
//                PreferenceGroup pGrp = (PreferenceGroup) p;
//                for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
//                    initSummary(pGrp.getPreference(i));
//                }
//            } else {
//                updatePrefSummary(p);
//            }
//        }
//
//        private void updatePrefSummary(Preference p) {
//            if (p instanceof ListPreference) {
//                ListPreference listPref = (ListPreference) p;
//                p.setSummary(listPref.getEntry());
//            }
//        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

//            updatePrefSummary(findPreference(key));
//            if (key.equals(KEY_PREF_RESET)) {
//                ...
//            }
        }

        private void resetApp() {
            DBServiceInterface service = DBService.getInstance(getActivity().getApplicationContext());
            service.reinitializeDatabase();
        }

    }
}
