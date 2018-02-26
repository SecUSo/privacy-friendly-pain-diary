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
package org.secuso.privacyfriendlypaindiary.database;

import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugIntakeInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Susanne Felsen
 * @version 20171229
 */
public interface DBServiceInterface {

    public void initializeDatabase();

    /**
     * Reinitializes the database. All data will be lost.
     */
    public void reinitializeDatabase();

    public long storeUser(UserInterface user);

    /**
     * Updates the given user.
     *
     * @param user user to update; must be persistent (see {@link UserInterface#isPersistent()})
     */
    public void updateUser(UserInterface user);

    public void deleteUser(UserInterface user);

    public UserInterface getUserByID(long id);

    public List<UserInterface> getAllUsers();

    public long storeDiaryEntryAndAssociatedObjects(DiaryEntryInterface diaryEntry);

    /**
     * Updates the given diary entry and associated pain description and drug intakes.
     *
     * @param diaryEntry diary entry to update; must be persistent, as does the pain description object
     */
    public void updateDiaryEntryAndAssociatedObjects(DiaryEntryInterface diaryEntry);

    public void deleteDiaryEntryAndAssociatedObjects(DiaryEntryInterface diaryEntry);

    public DiaryEntryInterface getDiaryEntryByID(long id);

    /**
     *
     * @return the ID of the latest diary entry or -1 if none has been made yet
     */
    public long getIDOfLatestDiaryEntry();

    public DiaryEntryInterface getDiaryEntryByDate(Date date);

    /**
     * Returns a list of all diary entries for the given month.
     *
     * @param month 1-based (1 = january, 2 = february and so on)
     * @param year
     * @return
     */
    public List<DiaryEntryInterface> getDiaryEntriesByMonth(int month, int year);

    /**
     * Returns a list of all diary entries for the given time span (including startDate and endDate).
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<DiaryEntryInterface> getDiaryEntriesByTimeSpan(Date startDate, Date endDate);

    /**
     * Returns a list of dates for all diary entries for the given month.
     *
     * @param month 1-based (1 = january, 2 = february and so on)
     * @param year
     * @return
     */
    public Set<Date> getDiaryEntryDatesByMonth(int month, int year);

    public Set<Date> getDiaryEntryDatesByTimeSpan(Date startDate, Date endDate);

    public Set<DrugIntakeInterface> getDrugIntakesForDiaryEntry(long diaryEntryID);

    public long storeDrug(DrugInterface drug);

    /**
     * Updates the given drug.
     *
     * @param drug drug to update; must be persistent (see {@link DrugInterface#isPersistent()})
     */
    public void updateDrug(DrugInterface drug);

    /**
     * Deletes the drug from the database if there are no more references to it.
     *
     * @param drug drug to delete
     */
    public void deleteDrug(DrugInterface drug);

    public DrugInterface getDrugByID(long id);

    public DrugInterface getDrugByNameAndDose(String name, String dose);

    public List<DrugInterface> getAllDrugs();

    public List<DrugInterface> getAllCurrentlyTakenDrugs();

}
