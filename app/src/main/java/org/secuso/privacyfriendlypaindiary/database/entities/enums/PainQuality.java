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
package org.secuso.privacyfriendlypaindiary.database.entities.enums;

import org.secuso.privacyfriendlypaindiary.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Represents the quality of the pain experienced by a user, i.e. how he would
 * characterize the nature of his pain.
 *
 * @author Susanne Felsen
 * @version 20171205
 */
public enum PainQuality {
    STABBING("stabbing", R.string.pain_stabbing), DULL("dull", R.string.pain_dull), SHOOTING("shooting", R.string.pain_shooting), BURNING("burning", R.string.pain_burning), THROBBING("throbbing", R.string.pain_throbbing);

    private String stringValue;
    private int resourceID;
    private static Map<String, PainQuality> map = new HashMap<>();

    PainQuality(String stringValue, int resourceID) {
        this.stringValue = stringValue;
        this.resourceID = resourceID;
    }

    static {
        for (PainQuality q : PainQuality.values()) {
            map.put(q.stringValue, q);
        }
    }

    public static PainQuality fromString(String quality) {
        return map.get(quality);
    }

    public String toString() {
        return stringValue;
    }

    /**
     * Returns the resource ID of the corresponding string resource.
     *
     * @return resource ID
     */
    public int getResourceID() {
        return resourceID;
    }

}