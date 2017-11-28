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
