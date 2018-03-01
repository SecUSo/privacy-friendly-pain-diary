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

import org.secuso.privacyfriendlypaindiary.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a region on a person's body.
 * <br/>
 * The values of this enum can be divided into two categories: body regions on
 * the front of a person's body and body regions on the back of a person's body.
 * All body regions with an index smaller than {@link BodyRegion#LOWEST_BACK_INDEX}
 * are body regions on the front of a person's body.
 *
 * @author Susanne Felsen
 * @version 20180301
 */
public enum BodyRegion {
    ABDOMEN_LEFT(0, R.drawable.paindiary_person_abdomen_left), ABDOMEN_RIGHT(1, R.drawable.paindiary_person_abdomen_right),
    GROIN_LEFT(2, R.drawable.paindiary_person_groin_left), GROIN_RIGHT(3, R.drawable.paindiary_person_groin_right),
    THIGH_LEFT(4, R.drawable.paindiary_person_thigh_left), THIGH_RIGHT(5, R.drawable.paindiary_person_thigh_right),
    KNEE_LEFT(6, R.drawable.paindiary_person_knee_left), KNEE_RIGHT(7, R.drawable.paindiary_person_knee_right),
    LOWER_LEG_LEFT(8, R.drawable.paindiary_person_leg_left), LOWER_LEG_RIGHT(9, R.drawable.paindiary_person_leg_right),
    FOOT_LEFT(10, R.drawable.paindiary_person_foot_left), FOOT_RIGHT(11, R.drawable.paindiary_person_foot_right),
    CHEST_LEFT(12, R.drawable.paindiary_person_chest_left), CHEST_RIGHT(13, R.drawable.paindiary_person_chest_right),
    NECK(14, R.drawable.paindiary_person_neck), HEAD(15, R.drawable.paindiary_person_head),
    UPPER_ARM_LEFT(16, R.drawable.paindiary_person_upperarm_left), UPPER_ARM_RIGHT(17, R.drawable.paindiary_person_upperarm_right),
    LOWER_ARM_LEFT(18, R.drawable.paindiary_person_lowerarm_left), LOWER_ARM_RIGHT(19, R.drawable.paindiary_person_lowerarm_right),
    HAND_LEFT(20, R.drawable.paindiary_person_hand_left), HAND_RIGHT(21, R.drawable.paindiary_person_hand_right),

    ABDOMEN_LEFT_BACK(22, R.drawable.paindiary_person_abdomen_left), ABDOMEN_RIGHT_BACK(23, R.drawable.paindiary_person_abdomen_right),
    GROIN_LEFT_BACK(24, R.drawable.paindiary_person_groin_left), GROIN_RIGHT_BACK(25, R.drawable.paindiary_person_groin_right),
    THIGH_LEFT_BACK(26, R.drawable.paindiary_person_thigh_left), THIGH_RIGHT_BACK(27, R.drawable.paindiary_person_thigh_right),
    KNEE_LEFT_BACK(28, R.drawable.paindiary_person_knee_left), KNEE_RIGHT_BACK(29, R.drawable.paindiary_person_knee_right),
    LOWER_LEG_LEFT_BACK(30, R.drawable.paindiary_person_leg_left), LOWER_LEG_RIGHT_BACK(31, R.drawable.paindiary_person_leg_right),
    FOOT_LEFT_BACK(32, R.drawable.paindiary_person_foot_left), FOOT_RIGHT_BACK(33, R.drawable.paindiary_person_foot_right),
    CHEST_LEFT_BACK(34, R.drawable.paindiary_person_chest_left), CHEST_RIGHT_BACK(35, R.drawable.paindiary_person_chest_right),
    NECK_BACK(36, R.drawable.paindiary_person_neck), HEAD_BACK(37, R.drawable.paindiary_person_head),
    UPPER_ARM_LEFT_BACK(38, R.drawable.paindiary_person_upperarm_left), UPPER_ARM_RIGHT_BACK(39, R.drawable.paindiary_person_upperarm_right),
    LOWER_ARM_LEFT_BACK(40, R.drawable.paindiary_person_lowerarm_left), LOWER_ARM_RIGHT_BACK(41, R.drawable.paindiary_person_lowerarm_right),
    HAND_LEFT_BACK(42, R.drawable.paindiary_person_hand_left), HAND_RIGHT_BACK(43, R.drawable.paindiary_person_hand_right);

    private int value;
    private int resourceID;
    private static Map<Integer, BodyRegion> map = new HashMap<>();
    public static final int LOWEST_BACK_INDEX = 22;

    BodyRegion(int value, int resourceID) {
        this.value = value;
        this.resourceID = resourceID;
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

    /**
     * Returns the resource ID of the corresponding drawable resource.
     *
     * @return resource ID
     */
    public int getResourceID() {
        return resourceID;
    }

}
