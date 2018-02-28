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
package org.secuso.privacyfriendlypaindiary.database.entities.interfaces;

import org.secuso.privacyfriendlypaindiary.database.entities.enums.Condition;

import java.util.Date;
import java.util.Set;

/**
 * Pain diary entry made by a user. A diary entry is identified by its date. There
 * can only be ony entry per day. It holds information entered by the user such as
 * his condition and additional notes. Every diary entry contains a
 * {@link PainDescriptionInterface} object that encapsulates information
 * about the pain he is experiencing. Furthermore, a set of {@link DrugIntakeInterface}
 * objects is associated with each diary entry. Each of these objects encapsulates
 * information about a specific drug taken in by the user (i.e. the quantity taken at
 * different times of day).
 * </p>
 * Since users are not required to enter any information, the getter-methods might
 * return <code>null</code>.
 *
 * @author Susanne Felsen
 * @version 20171124
 */
public interface DiaryEntryInterface extends PersistentObject {

    Date getDate();

    void setDate(Date date);

    Condition getCondition();

    void setCondition(Condition condition);

    PainDescriptionInterface getPainDescription();

    void setPainDescription(PainDescriptionInterface painDescription);

    String getNotes();

    void setNotes(String notes);

    Set<DrugIntakeInterface> getDrugIntakes();

    /**
     * Adds a drug intake to the set of drug intakes associated with this entry.
     * Calls {@link DrugIntakeInterface#setDiaryEntry(DiaryEntryInterface)}.
     *
     * @param intake drug intake to be added
     */
    void addDrugIntake(DrugIntakeInterface intake);

    void removeDrugIntake(DrugIntakeInterface intake);

    /**
     * Returns the drug intake with the given object ID from the set of drug intakes
     * associated with this entry.
     *
     * @param id object ID of the drug intake to remove
     * @return drug intake object or <code>null</code>
     */
    DrugIntakeInterface getDrugIntakeByID(long id);

}
