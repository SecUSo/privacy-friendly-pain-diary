package org.secuso.privacyfriendlyexample.database.entities.impl;

import org.secuso.privacyfriendlyexample.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlyexample.database.entities.enums.Condition;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.DrugInterface;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.PainDescriptionInterface;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Susanne Felsen
 * @version 20171118
 */
public class DiaryEntry extends AbstractPersistentObject implements DiaryEntryInterface {

    public static final String TABLE_NAME = "diaryentry";
    public static final String TABLE_ASSOCIATIVE_NAME = TABLE_NAME + "_" + Drug.TABLE_NAME;
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_CONDITION = "condition";
    public static final String COLUMN_NOTES = "notes";

    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE + " TEXT NOT NULL, " +
            PainDescription.TABLE_NAME + "_id INTEGER REFERENCES " + PainDescription.TABLE_NAME + "(" + COLUMN_ID + "), " +
            COLUMN_CONDITION + " INTEGER NOT NULL, " +
            COLUMN_NOTES + " TEXT);";

    public static final String TABLE_ASSOCIATIVE_CREATE = "CREATE TABLE " + TABLE_ASSOCIATIVE_NAME + "(" +
            TABLE_NAME + "_id INTEGER REFERENCES " + TABLE_NAME + "(" + COLUMN_ID + "), " +
            Drug.TABLE_NAME + "_id INTEGER REFERENCES " + Drug.TABLE_NAME + "(" + COLUMN_ID + "), " +
            Drug.COLUMN_MORNING + " INTEGER NOT NULL DEFAULT 0, " +
            Drug.COLUMN_NOON + " INTEGER NOT NULL DEFAULT 0, " +
            Drug.COLUMN_EVENING + " INTEGER NOT NULL DEFAULT 0, " +
            Drug.COLUMN_NIGHT + " INTEGER NOT NULL DEFAULT 0);";

    private Date date;
    private Condition condition;
    private PainDescriptionInterface painDescription;
    private String notes;
    private Set<DrugInterface> drugs;

    public DiaryEntry(Date date) {
        this.date = date;
        this.drugs = new HashSet<>();
    }

    public DiaryEntry(Date date, Condition condition, PainDescriptionInterface painDescription, String notes, Set<DrugInterface> drugs) {
        this.date = date;
        this.condition = condition;
        this.painDescription = painDescription;
        this.notes = notes;
        if(drugs != null) {
            this.drugs = drugs;
        } else {
            this.drugs = new HashSet<>();
        }
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public Condition getCondition() {
        return condition;
    }

    @Override
    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public PainDescriptionInterface getPainDescription() {
        return painDescription;
    }

    @Override
    public void setPainDescription(PainDescriptionInterface painDescription) {
        this.painDescription = painDescription;
    }

    @Override
    public String getNotes() {
        return notes;
    }

    @Override
    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public void addDrug(DrugInterface drug) {
        drugs.add(drug);
    }

    @Override
    public void removeDrug(DrugInterface drug) {
        drugs.remove(drug);
    }

}
