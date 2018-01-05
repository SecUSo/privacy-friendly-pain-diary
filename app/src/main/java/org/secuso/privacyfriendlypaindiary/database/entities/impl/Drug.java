package org.secuso.privacyfriendlypaindiary.database.entities.impl;

import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugInterface;

/**
 * @author Susanne Felsen
 * @version 20171124
 */
public class Drug extends AbstractPersistentObject implements DrugInterface {

    public static final String TABLE_NAME = "drug";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DOSE = "dose";
    public static final String COLUMN_CURRENTLY_TAKEN = "currentlytaken";

    public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_DOSE + " TEXT, " +
            COLUMN_CURRENTLY_TAKEN + " BOOLEAN NOT NULL CHECK(" + COLUMN_CURRENTLY_TAKEN + " IN (0,1)));";

    private String name;
    private String dose;
    private boolean currentlyTaken;

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
    public int hashCode() {
//        int result = super.hashCode();
        int result = 1;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (dose != null ? dose.hashCode() : 0);
        result = 31 * result + (currentlyTaken ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
//        if (!super.equals(o)) return false;

        Drug drug = (Drug) o;

//        if (currentlyTaken != drug.currentlyTaken) return false;
        if (name != null ? !name.equals(drug.name) : drug.name != null) return false;
        return dose != null ? dose.equals(drug.dose) : drug.dose == null;
    }

}
