package org.secuso.privacyfriendlypaindiary.database.entities.enums;

import org.secuso.privacyfriendlypaindiary.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Susanne Felsen
 * @version 20171205
 */
public enum PainQuality {
    STABBING("stabbing", R.string.pain_stabbing), DULL("dull", R.string.pain_dull), SHOOTING("shooting", R.string.pain_shooting);

    public static final EnumSet<PainQuality> ALL_OPTIONS = EnumSet.allOf(PainQuality.class);
    private String stringValue;
    private int resourceID;
    private static Map<String, PainQuality> map = new HashMap<>();

    private PainQuality(String stringValue, int resourceID) {
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

    public int getResourceID() {
        return resourceID;
    }

}