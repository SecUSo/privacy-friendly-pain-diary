package org.secuso.privacyfriendlypaindiary.database.entities.enums;

import org.secuso.privacyfriendlypaindiary.R;

import java.util.HashMap;
import java.util.Map;

/**
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