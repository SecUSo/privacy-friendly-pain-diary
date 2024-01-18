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

import android.content.Context;
import android.content.SharedPreferences;

import org.secuso.privacyfriendlypaindiary.database.entities.impl.AbstractPersistentObject;

/**
 * Instances of this class can be used to store or get values to or from shared preferences.
 * Specifically, it is recorded whether it is the app's first launch (which helps to determine
 * whether the tutorial should be shown), as well as the user ID for retrieving the user object
 * from the database.
 *
 * @author Karola Marky, Susanne Felsen
 * @version 20180228
 * <p>
 * Class structure taken from <a href="http://www.androidhive.info/2016/05/android-build-intro-slider-app/">this tutorial</a>.
 */
public class PrefManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    // shared pref mode
    public static final int PRIVATE_MODE = Context.MODE_PRIVATE;

    // Shared preferences file name
    public static final String PREF_NAME = "privacy_friendly_apps";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String USER_ID = "userID";

    public PrefManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setUserID(long userID) {
        editor.putLong(USER_ID, userID);
        editor.commit();
    }

    public long getUserID() {
        return pref.getLong(USER_ID, AbstractPersistentObject.INVALID_OBJECT_ID);
    }

}
