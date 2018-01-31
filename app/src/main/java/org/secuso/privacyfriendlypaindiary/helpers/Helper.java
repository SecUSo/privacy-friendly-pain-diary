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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.BodyRegion;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * @author Susanne Felsen
 * @version 20171230
 */
public class Helper {

    //ExportPDFActivity
    public static ArrayList<View> pdfViewList = new ArrayList<View>();
    public static void addViewExportPdf(View v){
        pdfViewList.add(v);
    }

    public static int getResourceIDForBodyRegion(BodyRegion bodyRegion) {
        switch(bodyRegion) {
            case ABDOMEN_RIGHT:
                return R.drawable.paindiary_person_abdomen_right;
            case ABDOMEN_LEFT:
                return R.drawable.paindiary_person_abdomen_left;
            case GROIN_LEFT:
                return R.drawable.paindiary_person_groin_left;
            case GROIN_RIGHT:
                return R.drawable.paindiary_person_groin_right;
            case THIGH_LEFT:
                return R.drawable.paindiary_person_thigh_left;
            case THIGH_RIGHT:
                return R.drawable.paindiary_person_thigh_right;
            case KNEE_LEFT:
                return R.drawable.paindiary_person_knee_left;
            case KNEE_RIGHT:
                return R.drawable.paindiary_person_knee_right;
            case LOWER_LEG_LEFT:
                return R.drawable.paindiary_person_leg_left;
            case LOWER_LEG_RIGHT:
                return R.drawable.paindiary_person_leg_right;
            case FOOT_LEFT:
                return R.drawable.paindiary_person_foot_left;
            case FOOT_RIGHT:
                return R.drawable.paindiary_person_foot_right;
            case CHEST_LEFT:
                return R.drawable.paindiary_person_chest_left;
            case CHEST_RIGHT:
                return R.drawable.paindiary_person_chest_right;
            case NECK:
                return R.drawable.paindiary_person_neck;
            case HEAD:
                return R.drawable.paindiary_person_head;
            case UPPER_ARM_LEFT:
                return R.drawable.paindiary_person_upperarm_left;
            case UPPER_ARM_RIGHT:
                return R.drawable.paindiary_person_upperarm_right;
            case LOWER_ARM_LEFT:
                return R.drawable.paindiary_person_lowerarm_left;
            case LOWER_ARM_RIGHT:
                return R.drawable.paindiary_person_lowerarm_right;
            case HAND_LEFT:
                return R.drawable.paindiary_person_hand_left;
            case HAND_RIGHT:
                return R.drawable.paindiary_person_hand_right;
            default:
                return 0;
        }
    }



    public static Bitmap overlay(Bitmap[] images) {
        Bitmap overlay = Bitmap.createBitmap(images[0].getWidth(), images[0].getHeight(), images[0].getConfig());
        Canvas canvas = new Canvas(overlay);
        for(int i = 0; i < images.length; i++) {
            canvas.drawBitmap(images[i], 0, 0, null);
        }
        return overlay;
    }

}
