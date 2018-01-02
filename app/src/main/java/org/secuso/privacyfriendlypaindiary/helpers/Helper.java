package org.secuso.privacyfriendlypaindiary.helpers;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.BodyRegion;

/**
 * @author Susanne Felsen
 * @version 20171230
 */
public class Helper {

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
}
