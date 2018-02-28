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

import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugIntakeInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugInterface;

/**
 * Instances of this class store in which quantity a specific drug
 * (represented by the associated {@link DrugInterface} object) is
 * taken in by the user at different times of day. Every drug intake is
 * associated with a diary entry.
 *
 * @author Susanne Felsen
 * @version 20171124
 */
public class DrugIntake extends AbstractPersistentObject implements DrugIntakeInterface {

    public static final String TABLE_NAME = "drugintake";
    public static final String COLUMN_MORNING = "morning";
    public static final String COLUMN_NOON = "noon";
    public static final String COLUMN_EVENING = "evening";
    public static final String COLUMN_NIGHT = "night";

    public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_MORNING + " INT NOT NULL DEFAULT 0, " +
            COLUMN_NOON + " INT NOT NULL DEFAULT 0, " +
            COLUMN_EVENING + " INT NOT NULL DEFAULT 0, " +
            COLUMN_NIGHT + " INT NOT NULL DEFAULT 0, " +
            Drug.TABLE_NAME + "_id INTEGER REFERENCES " + Drug.TABLE_NAME + "(" + COLUMN_ID + ")," +
            DiaryEntry.TABLE_NAME + "_id INTEGER REFERENCES " + DiaryEntry.TABLE_NAME + "(" + COLUMN_ID + "));";

    private DiaryEntryInterface diaryEntry;
    private DrugInterface drug;
    private int quantityMorning;
    private int quantityNoon;
    private int quantityEvening;
    private int quantityNight;

    public DrugIntake(DrugInterface drug, int quantityMorning, int quantityNoon, int quantityEvening, int quantityNight) {
        this.drug = drug;
        this.quantityMorning = quantityMorning;
        this.quantityNoon = quantityNoon;
        this.quantityEvening = quantityEvening;
        this.quantityNight = quantityNight;
    }

    public DrugIntake(DrugIntakeInterface intake) {
        this.drug = intake.getDrug();
        this.quantityMorning = intake.getQuantityMorning();
        this.quantityNoon = intake.getQuantityNoon();
        this.quantityEvening = intake.getQuantityEvening();
        this.quantityNight = intake.getQuantityNight();
    }

    @Override
    public DiaryEntryInterface getDiaryEntry() {
        return diaryEntry;
    }

    @Override
    public void setDiaryEntry(DiaryEntryInterface diaryEntry) {
        this.diaryEntry = diaryEntry;
    }

    @Override
    public DrugInterface getDrug() {
        return drug;
    }

    @Override
    public int getQuantityMorning() {
        return quantityMorning;
    }

    @Override
    public void setQuantityMorning(int quantityMorning) {
        this.quantityMorning = quantityMorning;
    }

    @Override
    public int getQuantityNoon() {
        return quantityNoon;
    }

    @Override
    public void setQuantityNoon(int quantityNoon) {
        this.quantityNoon = quantityNoon;
    }

    @Override
    public int getQuantityEvening() {
        return quantityEvening;
    }

    @Override
    public void setQuantityEvening(int quantityEvening) {
        this.quantityEvening = quantityEvening;
    }

    @Override
    public int getQuantityNight() {
        return quantityNight;
    }

    @Override
    public void setQuantityNight(int quantityNight) {
        this.quantityNight = quantityNight;
    }

    @Override
    public int hashCode() {
//        int result = super.hashCode();
        int result = 1;
        result = 31 * result + (drug != null ? drug.hashCode() : 0);
        result = 31 * result + quantityMorning;
        result = 31 * result + quantityNoon;
        result = 31 * result + quantityEvening;
        result = 31 * result + quantityNight;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
//        if (!super.equals(o)) return false;

        DrugIntake that = (DrugIntake) o;

        if (quantityMorning != that.quantityMorning) return false;
        if (quantityNoon != that.quantityNoon) return false;
        if (quantityEvening != that.quantityEvening) return false;
        if (quantityNight != that.quantityNight) return false;
        return drug != null ? drug.equals(that.drug) : that.drug == null;
    }

}
