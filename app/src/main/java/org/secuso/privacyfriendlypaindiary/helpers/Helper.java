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
package org.secuso.privacyfriendlypaindiary.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.BodyRegion;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.PainQuality;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Time;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugIntakeInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.PainDescriptionInterface;

import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.Iterator;

/**
 * Assortment of static helper methods, e.g. to obtain the resource ID of a
 * resource associated with an enum value or to convert enum sets to a string
 * representation.
 *
 * @author Susanne Felsen
 * @version 20180301
 */
public class Helper {

    public static Bitmap overlay(Context context, EnumSet<BodyRegion> bodyRegions) {
        Bitmap overlay = null;
        Iterator<BodyRegion> it = bodyRegions.iterator();
        if(it.hasNext()) {
            Bitmap img = BitmapFactory.decodeResource(context.getResources(), it.next().getResourceID());
            overlay = Bitmap.createBitmap(img.getWidth(), img.getHeight(), img.getConfig());
            if(overlay != null) {
                Canvas canvas = new Canvas(overlay);
                canvas.drawBitmap(img, 0, 0, null);
                img.recycle();

                while (it.hasNext()) {
                    img = BitmapFactory.decodeResource(context.getResources(), it.next().getResourceID());
                    canvas.drawBitmap(img, 0, 0, null);
                    img.recycle();
                }
            }
        }
        return overlay;
    }

    public static Bitmap overlay(Bitmap bitmap, Bitmap bitmapToAdd) {
        Bitmap overlay = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(overlay);
        canvas.drawBitmap(bitmap, 0, 0, null);
        bitmap.recycle();
        canvas.drawBitmap(bitmapToAdd, 0, 0, null);
        bitmapToAdd.recycle();
        return overlay;
    }

    public static View getDiaryEntrySummary(Context context, DiaryEntryInterface diaryEntry) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.diaryentry_summary, null);

        PainDescriptionInterface painDescription = diaryEntry.getPainDescription();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        ((TextView) view.findViewById(R.id.date)).setText(dateFormat.format(diaryEntry.getDate()));
        if(diaryEntry.getNotes() != null) {
            ((TextView) view.findViewById(R.id.notes_value)).setText(diaryEntry.getNotes());
        }
        if(diaryEntry.getCondition() != null) {
            ((ImageView) view.findViewById(R.id.condition_icon)).setImageResource(diaryEntry.getCondition().getResourceID());
        }
        if(painDescription != null) {
            ((TextView) view.findViewById(R.id.painlevel_value)).setText(Integer.toString(painDescription.getPainLevel()));
            EnumSet<BodyRegion> bodyRegions = painDescription.getBodyRegions();
            EnumSet<BodyRegion> bodyRegionsFront = EnumSet.noneOf(BodyRegion.class);
            EnumSet<BodyRegion> bodyRegionsBack = EnumSet.noneOf(BodyRegion.class);
            // body regions are split up into two separate sets (front and back)
            for(BodyRegion region : bodyRegions) {
                if(region.getValue() < BodyRegion.LOWEST_BACK_INDEX) {
                    bodyRegionsFront.add(region);
                } else {
                    bodyRegionsBack.add(region);
                }
            }
            if(!bodyRegionsFront.isEmpty()) {
                ((ImageView) view.findViewById(R.id.bodyregion_value)).setImageBitmap(Helper.overlay(context, bodyRegionsFront));
                view.findViewById(R.id.bodyregion_value).setVisibility(View.VISIBLE);
            }
            if(!bodyRegionsBack.isEmpty()) {
                ((ImageView) view.findViewById(R.id.bodyregion_back_value)).setImageBitmap(Helper.overlay(context, bodyRegionsBack));
                view.findViewById(R.id.bodyregion_back_value).setVisibility(View.VISIBLE);
            }
            String painQualities = convertPainQualityEnumSetToString(context, painDescription.getPainQualities());
            if(painQualities != null) {
                ((TextView) view.findViewById(R.id.painquality_value)).setText(painQualities);
            }
            String timesOfPain = convertTimeEnumSetToString(context, painDescription.getTimesOfPain());
            if(timesOfPain != null) {
                ((TextView) view.findViewById(R.id.timeofpain_value)).setText(timesOfPain);
            }
        }
        String medication = "";
        for(DrugIntakeInterface drugIntake : diaryEntry.getDrugIntakes()) {
            if(drugIntake.getDrug().getName() != null) {
                medication += drugIntake.getDrug().getName();
            }
            if(drugIntake.getDrug().getDose() != null) {
                medication += " (" + drugIntake.getDrug().getDose() + ") ";
            }
            medication += ": " + drugIntake.getQuantityMorning() + " " + drugIntake.getQuantityNoon() + " " + drugIntake.getQuantityEvening() + " " + drugIntake.getQuantityNight() +
                        System.getProperty("line.separator");
        }
        if(!medication.isEmpty()) {
            ((TextView) view.findViewById(R.id.medication_value)).setText(medication);
        }

        return view;
    }

    public static String convertPainQualityEnumSetToString(Context context, EnumSet<PainQuality> painQualities) {
        String painQualitiesAsString = "";
        for(PainQuality quality : painQualities) {
            painQualitiesAsString += context.getString(quality.getResourceID()) + ", ";
        }
        if(!painQualitiesAsString.isEmpty()) {
            painQualitiesAsString = painQualitiesAsString.substring(0, painQualitiesAsString.length() - 2);
        } else {
            painQualitiesAsString = null;
        }
        return painQualitiesAsString;
    }

    public static String convertTimeEnumSetToString(Context context, EnumSet<Time> times) {
        String timesAsString = "";
        for(Time time : times) {
            timesAsString += context.getString(time.getResourceID()) + ", ";
        }
        if(!timesAsString.isEmpty()) {
            timesAsString = timesAsString.substring(0, timesAsString.length() - 2);
        } else {
            timesAsString = null;
        }
        return timesAsString;
    }

}
