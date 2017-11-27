package org.secuso.privacyfriendlyexample.database.entities.impl;

import org.secuso.privacyfriendlyexample.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlyexample.database.entities.enums.Condition;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.DrugIntakeInterface;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.DrugInterface;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.PainDescriptionInterface;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Susanne Felsen
 * @version 20171124
 */
public class DiaryEntry extends AbstractPersistentObject implements DiaryEntryInterface {

    public static final String TABLE_NAME = "diaryentry";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_CONDITION = "condition";
    public static final String COLUMN_NOTES = "notes";

    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE + " TEXT UNIQUE NOT NULL, " +
            PainDescription.TABLE_NAME + "_id INTEGER REFERENCES " + PainDescription.TABLE_NAME + "(" + COLUMN_ID + "), " +
            COLUMN_CONDITION + " INTEGER NOT NULL, " +
            COLUMN_NOTES + " TEXT);";

    private Date date;
    private Condition condition;
    private PainDescriptionInterface painDescription;
    private String notes;
    private Set<DrugIntakeInterface> intakes = new HashSet<>();

    public DiaryEntry(Date date) {
        this.date = date;
    }

    public DiaryEntry(Date date, Condition condition, PainDescriptionInterface painDescription, String notes, Set<DrugIntakeInterface> intakes) {
        this.date = date;
        this.condition = condition;
        this.painDescription = painDescription;
        this.notes = notes;
        if(intakes != null) {
            for(DrugIntakeInterface intake : intakes) {
                addDrugIntake(intake);
            }
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
    public Set<DrugIntakeInterface> getDrugIntakes() {
        //TODO unmodifiable?
        return Collections.unmodifiableSet(intakes);
    }

    @Override
    public void addDrugIntake(DrugIntakeInterface intake) {
        intakes.add(intake);
        intake.setDiaryEntry(this);
    }

    @Override
    public void removeDrugIntake(DrugIntakeInterface intake) {
        intakes.remove(intake);
        intake.setDiaryEntry(null);
    }

}
