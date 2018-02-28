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
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Condition;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugIntakeInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.PainDescriptionInterface;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Instances of this class represent pain diary entries made by a user.
 * A diary entry is identified by its date. There can only be ony entry
 * per day. It holds information entered by the user such as his condition
 * and additional notes. Every diary entry contains a {@link PainDescriptionInterface}
 * object that encapsulates information about the pain he is experiencing.
 * Furthermore, a set of {@link DrugIntakeInterface} objects is associated
 * with each diary entry. Each of these objects encapsulates information
 * about a specific drug taken in by the user (i.e. the quantity taken at
 * different times of day).
 * </p>
 * Since users are not required to enter any information, some of the
 * fields might be <code>null</code>.
 *
 * @author Susanne Felsen
 * @version 20171124
 */
public class DiaryEntry extends AbstractPersistentObject implements DiaryEntryInterface {

    public static final String TABLE_NAME = "diaryentry";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_CONDITION = "condition";
    public static final String COLUMN_NOTES = "notes";

    public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE + " TEXT UNIQUE NOT NULL, " +
            PainDescription.TABLE_NAME + "_id INTEGER REFERENCES " + PainDescription.TABLE_NAME + "(" + COLUMN_ID + "), " +
            COLUMN_CONDITION + " INTEGER, " +
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

    @Override
    public DrugIntakeInterface getDrugIntakeByID(long id) {
        for(DrugIntakeInterface intake : intakes) {
            if(intake.getObjectID() == id) {
                return intake;
            }
        }
        return null;
    }

}
