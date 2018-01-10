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
    private EnumSet<BodyRegion> bodyRegions;

    private EnumSet<PainQuality> painQualities ;
    private EnumSet<Time> timesOfPain;

    public PainDescription(int painLevel, EnumSet<BodyRegion> bodyRegions) {
        this.painLevel = painLevel;
        this.bodyRegions = bodyRegions;
        this.painQualities = EnumSet.noneOf(PainQuality.class);
        this.timesOfPain = EnumSet.noneOf(Time.class);
    }

    public PainDescription(int painLevel, EnumSet<BodyRegion> bodyRegions, EnumSet<PainQuality> painQualities, EnumSet<Time> timesOfPain) {
        this.painLevel = painLevel;
        this.bodyRegions = bodyRegions;
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
    public EnumSet<BodyRegion> getBodyRegions() {
        return bodyRegions;
    }

    @Override
    public void setBodyRegions(EnumSet<BodyRegion> bodyRegions) {
        this.bodyRegions = bodyRegions;
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
