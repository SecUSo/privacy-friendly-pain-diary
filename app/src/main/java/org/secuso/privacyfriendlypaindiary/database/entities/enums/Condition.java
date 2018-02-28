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
 * @version 20171117
 */
public enum Condition {
    VERY_BAD(0), BAD(1), OKAY(2), GOOD(3), VERY_GOOD(4);

    private int value;
    private static Map<Integer, Condition> map = new HashMap<>();

    private Condition(int value) {
        this.value = value;
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

}