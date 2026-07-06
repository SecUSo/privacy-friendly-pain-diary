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
package org.secuso.privacyfriendlypaindiary.database.utils;

import android.util.Log;

import org.secuso.privacyfriendlypaindiary.database.entities.enums.BodyRegion;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.PainQuality;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Time;

import java.util.EnumSet;

public class Utils {
    private static final String TAG = "Utils";

    public static String convertBodyRegionEnumSetToString(EnumSet<BodyRegion> bodyRegions) {
        String bodyRegionsAsString = "";
        for (BodyRegion region : bodyRegions) {
            bodyRegionsAsString += region.getValue() + ",";
        }
        if (!bodyRegionsAsString.isEmpty()) {
            bodyRegionsAsString = bodyRegionsAsString.substring(0, bodyRegionsAsString.length() - 1);
        } else {
            bodyRegionsAsString = null;
        }
        return bodyRegionsAsString;
    }

    public static EnumSet<BodyRegion> convertStringToBodyRegionEnumSet(String bodyRegionsAsString) {
        EnumSet<BodyRegion> bodyRegions = EnumSet.noneOf(BodyRegion.class);
        if (bodyRegionsAsString != null && !bodyRegionsAsString.isEmpty()) {
            String[] regions = bodyRegionsAsString.split(",");
            for (String bodyRegion : regions) {
                try {
                    BodyRegion r = BodyRegion.valueOf(Integer.parseInt(bodyRegion));
                    bodyRegions.add(r);
                } catch (IllegalArgumentException e) {
                    Log.d(TAG, "Error parsing body region.");
                }
            }
        }
        return bodyRegions;
    }

    public static String convertPainQualityEnumSetToString(EnumSet<PainQuality> painQualities) {
        String painQualitiesAsString = "";
        for (PainQuality quality : painQualities) {
            painQualitiesAsString += quality.toString() + ",";
        }
        if (!painQualitiesAsString.isEmpty()) {
            painQualitiesAsString = painQualitiesAsString.substring(0, painQualitiesAsString.length() - 1);
        } else {
            painQualitiesAsString = null;
        }
        return painQualitiesAsString;
    }

    public static EnumSet<PainQuality> convertStringToPainQualityEnumSet(String painQualitiesAsString) {
        EnumSet<PainQuality> painQualities = EnumSet.noneOf(PainQuality.class);
        if (painQualitiesAsString != null && !painQualitiesAsString.isEmpty()) {
            String[] qualities = painQualitiesAsString.split(",");
            for (String quality : qualities) {
                PainQuality q = PainQuality.fromString(quality);
                if (q != null) painQualities.add(q);
            }
        }
        return painQualities;
    }

    public static String convertTimeEnumSetToString(EnumSet<Time> times) {
        String timesAsString = "";
        for (Time time : times) {
            timesAsString += time.toString() + ",";
        }
        if (!timesAsString.isEmpty()) {
            timesAsString = timesAsString.substring(0, timesAsString.length() - 1);
        } else {
            timesAsString = null;
        }
        return timesAsString;
    }

    public static EnumSet<Time> convertStringToTimeEnumSet(String timesAsString) {
        EnumSet<Time> timesOfPain = EnumSet.noneOf(Time.class);
        if (timesAsString != null && !timesAsString.isEmpty()) {
            String[] times = timesAsString.split(",");
            for (String time : times) {
                Time t = Time.fromString(time);
                if (t != null) timesOfPain.add(t);
            }
        }
        return timesOfPain;
    }
}
