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
import android.preference.PreferenceManager;

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
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    private static final String USER_ID = "userID";

    public PrefManager(Context context) {
        // Store the user id in the default preferences so it is included in the PFA-Core backup
        // (its own file used to be left out, which made restores lose the user details).
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
    }

    public void setUserID(long userID) {
        // PFA-Core preferences support Int but not Long; the user id always fits in an Int.
        editor.putInt(USER_ID, (int) userID);
        editor.commit();
    }

    public long getUserID() {
        return pref.getInt(USER_ID, (int) AbstractPersistentObject.INVALID_OBJECT_ID);
    }

}
