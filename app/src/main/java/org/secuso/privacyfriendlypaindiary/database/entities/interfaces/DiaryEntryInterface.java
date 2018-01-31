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
 * @author Susanne Felsen
 * @version 20171124
 */
public interface DiaryEntryInterface extends PersistentObject {

    public Date getDate();

    public void setDate(Date date);

    public Condition getCondition();

    public void setCondition(Condition condition);

    public PainDescriptionInterface getPainDescription();

    public void setPainDescription(PainDescriptionInterface painDescription);

    public String getNotes();

    public void setNotes(String notes);

    public Set<DrugIntakeInterface> getDrugIntakes();

    /**
     * Adds a drug intake to the set of drug intakes associated with this entry.
     * The set must not contain another drug intake associated with the same drug.
     *
     * @param intake drug intake to be added
     */
    public void addDrugIntake(DrugIntakeInterface intake);

    public void removeDrugIntake(DrugIntakeInterface intake);

    /**
     * @param id
     * @return drug intake object or <code>null</code>
     */
    public DrugIntakeInterface getDrugIntakeByID(long id);

}
