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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Susanne Felsen
 * @version 20171117
 */
public enum Gender {
    MALE(0), FEMALE(1), OTHER(2);

    private int value;
    private static Map<Integer, Gender> map = new HashMap<>();

    private Gender(int value) {
        this.value = value;
    }

    static {
        for (Gender g : Gender.values()) {
            map.put(g.value, g);
        }
    }

    public static Gender valueOf(int gender) {
        return map.get(gender);
    }

    public int getValue() {
        return value;
    }

//  @Override
//  public String toString() {
//      TODO: return localized String
//  }

}
