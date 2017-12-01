package org.secuso.privacyfriendlypaindiary.database.entities.impl;

import org.secuso.privacyfriendlypaindiary.database.entities.enums.BodyRegion;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.PainDescriptionInterface;

/**
 * @author Susanne Felsen
 * @version 20171118
 */
public class PainDescription extends AbstractPersistentObject implements PainDescriptionInterface {

    public static final String TABLE_NAME = "paindescription";
    public static final String COLUMN_PAIN_LEVEL = "painlevel";
    public static final String COLUMN_BODY_REGION = "bodyregion";

    public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PAIN_LEVEL + " INTEGER NOT NULL, " +
            COLUMN_BODY_REGION + " INTEGER NOT NULL);";

    //TODO: bidirectional association to diary entry?

    private int painLevel;
    private BodyRegion bodyRegion;
    /* {stabbing, dull, shooting, ... }
     * {stechend, dumpf, einschießend, ... } */
    private boolean[] character = {false, false, false};
    //TODO: time

    public PainDescription(int painLevel) {
        this.painLevel = painLevel;
    }

    public PainDescription(int painLevel, BodyRegion bodyRegion) {
        this.painLevel = painLevel;
        this.bodyRegion = bodyRegion;
    }

    @Override
    public int getPainLevel() {
        return painLevel;
    }

    @Override
    public void setPainLevel(int painLevel) {
        this.painLevel = painLevel;
    }

    @Override
    public BodyRegion getBodyRegion() {
        return bodyRegion;
    }

    @Override
    public void setBodyRegion(BodyRegion bodyRegion) {
        this.bodyRegion = bodyRegion;
    }

}
