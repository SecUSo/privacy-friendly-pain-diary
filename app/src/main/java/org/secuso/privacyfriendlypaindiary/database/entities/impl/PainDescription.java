package org.secuso.privacyfriendlypaindiary.database.entities.impl;

import org.secuso.privacyfriendlypaindiary.database.entities.enums.BodyRegion;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.PainQuality;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Time;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.PainDescriptionInterface;

import java.util.EnumSet;

/**
 * @author Susanne Felsen
 * @version 20171205
 */
public class PainDescription extends AbstractPersistentObject implements PainDescriptionInterface {

    public static final String TABLE_NAME = "paindescription";
    public static final String COLUMN_PAIN_LEVEL = "painlevel";
    public static final String COLUMN_BODY_REGION = "bodyregion";
    public static final String COLUMN_PAIN_QUALITY = "painquality";
    public static final String COLUMN_TIME_OF_PAIN = "timeofpain";

    public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PAIN_LEVEL + " INTEGER NOT NULL, " +
            COLUMN_BODY_REGION + " INTEGER, " +
            COLUMN_PAIN_QUALITY + " STRING, " +
            COLUMN_TIME_OF_PAIN + " STRING);";

    private int painLevel;
    private BodyRegion bodyRegion;

    private EnumSet<PainQuality> painQualities ;
    private EnumSet<Time> timesOfPain;

    public PainDescription(int painLevel, BodyRegion bodyRegion) {
        this.painLevel = painLevel;
        this.bodyRegion = bodyRegion;
        this.painQualities = EnumSet.noneOf(PainQuality.class);
        this.timesOfPain = EnumSet.noneOf(Time.class);
    }

    public PainDescription(int painLevel, BodyRegion bodyRegion, EnumSet<PainQuality> painQualities, EnumSet<Time> timesOfPain) {
        this.painLevel = painLevel;
        this.bodyRegion = bodyRegion;
        this.painQualities = painQualities;
        this.timesOfPain = timesOfPain;
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

    @Override
    public EnumSet<PainQuality> getPainQualities() {
        return painQualities;
    }

    @Override
    public void setPainQualities(EnumSet<PainQuality> painQualities) {
        this.painQualities = painQualities;
    }

    @Override
    public EnumSet<Time> getTimesOfPain() {
        return timesOfPain;
    }

    @Override
    public void setTimesOfPain(EnumSet<Time> timesOfPain) {
        this.timesOfPain = timesOfPain;
    }

}
