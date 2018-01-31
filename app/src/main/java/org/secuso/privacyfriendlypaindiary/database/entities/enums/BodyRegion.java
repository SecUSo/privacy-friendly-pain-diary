/*
    This file is part of Privacy Friendly Pain Diary.

    Privacy Friendly Pain Diary is licensed under the GPLv3.
    Copyright (C) 2018  Susanne Felsen, Rybien Sinjari

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

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
public enum BodyRegion {
    ABDOMEN_RIGHT(0), ABDOMEN_LEFT(1), GROIN_LEFT(2), GROIN_RIGHT(3), THIGH_LEFT(4), THIGH_RIGHT(5), KNEE_LEFT(6), KNEE_RIGHT(7), LOWER_LEG_LEFT(8), LOWER_LEG_RIGHT(9), FOOT_LEFT(10), FOOT_RIGHT(11), CHEST_LEFT(12), CHEST_RIGHT(13), NECK(14), HEAD(15), UPPER_ARM_LEFT(16), UPPER_ARM_RIGHT(17), LOWER_ARM_LEFT(18), LOWER_ARM_RIGHT(19), HAND_LEFT(20), HAND_RIGHT(21);

    private int value;
    private static Map<Integer, BodyRegion> map = new HashMap<>();

    private BodyRegion(int value) {
        this.value = value;
    }

    static {
        for (BodyRegion r : BodyRegion.values()) {
            map.put(r.value, r);
        }
    }

    public static BodyRegion valueOf(int region) {
        return map.get(region);
    }

    public int getValue() {
        return value;
    }

}
