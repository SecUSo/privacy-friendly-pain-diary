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
package org.secuso.privacyfriendlypaindiary.database.entities.impl;

import org.secuso.privacyfriendlypaindiary.database.entities.enums.BodyRegion;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.PainQuality;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Time;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.PainDescriptionInterface;

import java.util.EnumSet;

/**
 * Instances of this class encapsulate information about the pain a user is
 * experiencing, i.e. the intensity, nature (qualities) and times of the pain,
 * as well as the affected body regions.
 * </p>
 * Since users are not required to enter any information, some of the
 * fields might contain empty sets or be <code>null</code>.
 *
 * @author Susanne Felsen
 * @version 20180228
 */
public class PainDescription extends AbstractPersistentObject implements PainDescriptionInterface {

    public static final String TABLE_NAME = "paindescription";
    public static final String COLUMN_PAIN_LEVEL = "painlevel";
    public static final String COLUMN_BODY_REGIONS = "bodyregions";
    public static final String COLUMN_PAIN_QUALITIES = "painqualities";
    public static final String COLUMN_TIMES_OF_PAIN = "timesofpain";

    public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PAIN_LEVEL + " INTEGER NOT NULL, " +
            COLUMN_BODY_REGIONS + " STRING, " +
            COLUMN_PAIN_QUALITIES + " STRING, " +
            COLUMN_TIMES_OF_PAIN + " STRING);";

    private int painLevel;
    private EnumSet<BodyRegion> bodyRegions;

    private EnumSet<PainQuality> painQualities;
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
