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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.DBServiceInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.AbstractPersistentObject;
import org.secuso.privacyfriendlypaindiary.helpers.NotificationJobService;
import org.secuso.privacyfriendlypaindiary.tutorial.PrefManager;

/**
 * Inspiration from: <a href="https://developer.android.com/guide/topics/ui/settings.html"/> and <a href="https://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-su/4325239#4325239"/>
 *
 * @author Susanne Felsen
 * @version 20180130
 */
public class SettingsActivity extends BaseActivity {

    private static final String KEY_PREF_RESET = "pref_reset";
    public static final String KEY_PREF_MEDICATION = "pref_medication";
    private static final String KEY_PREF_REMINDER = "pref_reminder";
    private static final String KEY_PREF_REMINDER_TIME = "pref_reminder_time";

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

            initPreferences();
        }

        private void initPreferences() {
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
//            if(key.equals(KEY_PREF_RESET)) {
//
//            }
//            if(key.equals(KEY_PREF_MEDICATION)) {
//
//            }
            if (key.equals(KEY_PREF_REMINDER)) {
                boolean enabled = sharedPreferences.getBoolean(KEY_PREF_REMINDER, false);
                if(enabled) {
                    NotificationJobService.scheduleJob(getActivity().getApplicationContext());
                } else {
                    NotificationJobService.cancelJob(getActivity().getApplicationContext());
                }
            } else if (key.equals(KEY_PREF_REMINDER_TIME)) {
                boolean enabled = sharedPreferences.getBoolean(KEY_PREF_REMINDER, false);
                if(enabled) {
                    NotificationJobService.cancelJob(getActivity().getApplicationContext());
                    NotificationJobService.scheduleJob(getActivity().getApplicationContext());
                }
            }
        }

        private void resetApp() {
            DBServiceInterface service = DBService.getInstance(getActivity().getApplicationContext());
            service.reinitializeDatabase();

            resetPreferences();
        }

        private void resetPreferences() {
            PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit().clear().commit();
            PreferenceManager.setDefaultValues(getActivity().getApplicationContext(), R.xml.pref_general, true);
            NotificationJobService.cancelJob(getActivity().getApplicationContext());
            new PrefManager(getActivity().getApplicationContext()).setUserID(AbstractPersistentObject.INVALID_OBJECT_ID);
            getPreferenceScreen().removeAll();
            initPreferences();
        }

    }

}
