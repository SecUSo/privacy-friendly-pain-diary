package org.secuso.privacyfriendlyexample.database.entities.impl;

import org.secuso.privacyfriendlyexample.database.entities.interfaces.DrugInterface;

/**
 * @author Susanne Felsen
 * @version 20171118
 */
public class Drug extends AbstractPersistentObject implements DrugInterface {

    public static final String TABLE_NAME = "drug";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DOSE = "dose";
    public static final String COLUMN_CURRENTLY_TAKEN = "currentlytaken";

    //in associative table "diaryentry_drug" (see: class DiaryEntry)
    public static final String COLUMN_MORNING = "morning";
    public static final String COLUMN_NOON = "noon";
    public static final String COLUMN_EVENING = "evening";
    public static final String COLUMN_NIGHT = "night";

    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_DOSE + " TEXT, " +
            COLUMN_CURRENTLY_TAKEN + " BOOLEAN NOT NULL CHECK(" + COLUMN_CURRENTLY_TAKEN + " IN (0,1)));";

    private String name;
    private String dose;
    private boolean currentlyTaken;

    private int quantityMorning;
    private int quantityNoon;
    private int quantityEvening;
    private int quantityNight;

    public Drug(String name, String dose) {
        this.name = name;
        this.dose = dose;
        this.currentlyTaken = true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDose() {
        return dose;
    }

    @Override
    public void setDose(String dose) {
        this.dose = dose;
    }

    @Override
    public boolean isCurrentlyTaken() {
        return currentlyTaken;
    }

    @Override
    public void setCurrentlyTaken(boolean currentlyTaken) {
        this.currentlyTaken = currentlyTaken;
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
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (dose != null ? dose.hashCode() : 0);
        result = 31 * result + (currentlyTaken ? 1 : 0);
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
        if (!super.equals(o)) return false;

        Drug drug = (Drug) o;

        if (currentlyTaken != drug.currentlyTaken) return false;
        if (quantityMorning != drug.quantityMorning) return false;
        if (quantityNoon != drug.quantityNoon) return false;
        if (quantityEvening != drug.quantityEvening) return false;
        if (quantityNight != drug.quantityNight) return false;
        if (name != null ? !name.equals(drug.name) : drug.name != null) return false;
        return dose != null ? dose.equals(drug.dose) : drug.dose == null;
    }

}
