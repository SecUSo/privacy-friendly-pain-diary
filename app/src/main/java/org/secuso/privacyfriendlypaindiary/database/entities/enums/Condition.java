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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a user's condition, i.e. how he feels at the time of making a diary entry.
 *
 * @author Susanne Felsen
 * @version 20180301
 */
public enum Condition {
    VERY_BAD(0, R.drawable.ic_sentiment_very_dissatisfied), BAD(1, R.drawable.ic_sentiment_dissatisfied),
    OKAY(2, R.drawable.ic_sentiment_neutral), GOOD(3, R.drawable.ic_sentiment_satisfied),
    VERY_GOOD(4, R.drawable.ic_sentiment_very_satisfied);

    private int value;
    private int resourceID;
    private static Map<Integer, Condition> map = new HashMap<>();

    Condition(int value, int resourceID) {
        this.value = value;
        this.resourceID = resourceID;
    }

    static {
        for (Condition c : Condition.values()) {
            map.put(c.value, c);
        }
    }

    public static Condition valueOf(int condition) {
        return map.get(condition);
    }

    public int getValue() {
        return value;
    }

    /**
     * Returns the resource ID of the corresponding drawable resource.
     *
     * @return resource ID
     */
    public int getResourceID() {
        return resourceID;
    }

}