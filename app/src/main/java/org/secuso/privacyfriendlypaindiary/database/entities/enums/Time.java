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
import java.util.Map;

/**
 * Represents the time of the pain experienced by a user, i.e. when he feels pain.
 *
 * @author Susanne Felsen
 * @version 20171205
 */
public enum Time {
    ALL_DAY("all day", R.string.time_day), MORNING("morning", R.string.time_morning), AFTERNOON("afternoon", R.string.time_afternoon), EVENING("evening", R.string.time_evening), NIGHT("at night", R.string.time_night);

    private String stringValue;
    private int resourceID;
    private static Map<String, Time> map = new HashMap<>();

    Time(String stringValue, int resourceID) {
        this.stringValue = stringValue;
        this.resourceID = resourceID;
    }

    static {
        for (Time t : Time.values()) {
            map.put(t.stringValue, t);
        }
    }

    public static Time fromString(String time) {
        return map.get(time);
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
