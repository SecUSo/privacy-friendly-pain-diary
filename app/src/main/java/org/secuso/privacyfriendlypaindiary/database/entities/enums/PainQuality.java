package org.secuso.privacyfriendlypaindiary.database.entities.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Susanne Felsen
 * @version 20171205
 */
public enum PainQuality {
    STABBING("stabbing"), DULL("dull"), SHOOTING("shooting");

    public static final EnumSet<PainQuality> ALL_OPTIONS = EnumSet.allOf(PainQuality.class);
    private String stringValue;
    private static Map<String, PainQuality> map = new HashMap<>();

    private PainQuality(String stringValue) {
        this.stringValue = stringValue;
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

}