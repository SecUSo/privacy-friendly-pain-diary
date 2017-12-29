package org.secuso.privacyfriendlypaindiary.database.entities.enums;

import org.secuso.privacyfriendlypaindiary.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Susanne Felsen
 * @version 20171205
 */
public enum Time {
    ALL_DAY("all day", R.string.time_day), MORNING("morning", R.string.time_morning), AFTERNOON("afternoon", R.string.time_afternoon), EVENING("evening", R.string.time_evening);

    public static final EnumSet<Time> ALL_OPTIONS = EnumSet.allOf(Time.class);
    private String stringValue;
    private int resourceID;
    private static Map<String, Time> map = new HashMap<>();

    private Time(String stringValue, int resourceID) {
        this.stringValue = stringValue;
        this.resourceID = resourceID;
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

    public int getResourceID() {
        return resourceID;
    }

}
