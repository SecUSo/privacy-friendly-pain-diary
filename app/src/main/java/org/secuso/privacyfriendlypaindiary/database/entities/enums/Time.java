package org.secuso.privacyfriendlypaindiary.database.entities.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Susanne Felsen
 * @version 20171205
 */
public enum Time {
    ALL_DAY("all day"), MORNING("morning"), AFTERNOON("afternoon"), EVENING("evening");

    public static final EnumSet<Time> ALL_OPTIONS = EnumSet.allOf(Time.class);
    private String stringValue;
    private static Map<String, Time> map = new HashMap<>();

    private Time(String stringValue) {
        this.stringValue = stringValue;
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
}
