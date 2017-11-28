package org.secuso.privacyfriendlypaindiary.database.entities.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Susanne Felsen
 * @version 20171117
 */
public enum BodyRegion {
    //TODO: insert body regions, including everywhere + undefined
    HEAD(0), TORSO(1), LEFT_ARM(2), RIGHT_ARM(3), LEFT_LEG(4), RIGHT_LEG(5);

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

//  @Override
//  public String toString() {
//      TODO: return localized String
//  }

}
