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
                return R.drawable.schmerztagebuch_person_bauch_rechts;
            case ABDOMEN_LEFT:
                return R.drawable.schmerztagebuch_person_bauch_links;
            case GROIN_LEFT:
                return R.drawable.schmerztagebuch_person_leiste_links;
            case GROIN_RIGHT:
                return R.drawable.schmerztagebuch_person_leiste_rechts;
            case THIGH_LEFT:
                return R.drawable.schmerztagebuch_person_oberschenkel_links;
            case THIGH_RIGHT:
                return R.drawable.schmerztagebuch_person_oberschenkel_rechts;
            case KNEE_LEFT:
                return R.drawable.schmerztagebuch_person_knie_links;
            case KNEE_RIGHT:
                return R.drawable.schmerztagebuch_person_knie_rechts;
            case LOWER_LEG_LEFT:
                return R.drawable.schmerztagebuch_person_unterschenkel_links;
            case LOWER_LEG_RIGHT:
                return R.drawable.schmerztagebuch_person_unterschenkel_rechts;
            case FOOT_LEFT:
                return R.drawable.schmerztagebuch_person_fuss_links;
            case FOOT_RIGHT:
                return R.drawable.schmerztagebuch_person_fuss_rechts;
            case CHEST_LEFT:
                return R.drawable.schmerztagebuch_person_brust_links;
            case CHEST_RIGHT:
                return R.drawable.schmerztagebuch_person_brust_rechts;
            case NECK:
                return R.drawable.schmerztagebuch_person_hals;
            case HEAD:
                return R.drawable.schmerztagebuch_person_kopf;
            case UPPER_ARM_LEFT:
                return R.drawable.schmerztagebuch_person_oberarm_links;
            case UPPER_ARM_RIGHT:
                return R.drawable.schmerztagebuch_person_oberarm_rechts;
            case LOWER_ARM_LEFT:
                return R.drawable.schmerztagebuch_person_unterarm_links;
            case LOWER_ARM_RIGHT:
                return R.drawable.schmerztagebuch_person_unterarm_rechts;
            case HAND_LEFT:
                return R.drawable.schmerztagebuch_person_hand_links;
            case HAND_RIGHT:
                return R.drawable.schmerztagebuch_person_hand_rechts;
            default:
                return 0;
        }
    }
}
