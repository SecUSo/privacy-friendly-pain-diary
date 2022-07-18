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

import android.content.Context;

import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugIntakeInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Provides methods for storing, updating, retrieving and deleting users, drugs
 * and diary entries and associated objects.
 *
 * @author Susanne Felsen
 * @version 20171229
 */
public interface DBServiceInterface {

    void initializeDatabase();

    /**
     * Reinitializes the database. All data will be lost.
     */
    void reinitializeDatabase(Context context);

    long storeUser(UserInterface user);

    /**
     * Updates the given user.
     *
     * @param user user to update; must be persistent (see {@link UserInterface#isPersistent()})
     */
    void updateUser(UserInterface user);

    void deleteUser(UserInterface user);

    UserInterface getUserByID(long id);

    List<UserInterface> getAllUsers();

    long storeDiaryEntryAndAssociatedObjects(DiaryEntryInterface diaryEntry);

    /**
     * Updates the given diary entry and associated pain description and drug intakes.
     *
     * @param diaryEntry diary entry to update; must be persistent, as does the pain description object
     */
    void updateDiaryEntryAndAssociatedObjects(DiaryEntryInterface diaryEntry);

    void deleteDiaryEntryAndAssociatedObjects(DiaryEntryInterface diaryEntry);

    DiaryEntryInterface getDiaryEntryByID(long id);

    /**
     * @return the ID of the latest diary entry or -1 if none has been made yet
     */
    long getIDOfLatestDiaryEntry();

    DiaryEntryInterface getDiaryEntryByDate(Date date);

    /**
     * Returns a list of all diary entries for the given month.
     *
     * @param month 1-based (1 = january, 2 = february and so on)
     * @param year
     * @return
     */
    List<DiaryEntryInterface> getDiaryEntriesByMonth(int month, int year);

    /**
     * Returns a list of all diary entries for the given time span (including startDate and endDate).
     *
     * @param startDate
     * @param endDate
     * @return
     */
    List<DiaryEntryInterface> getDiaryEntriesByTimeSpan(Date startDate, Date endDate);

    /**
     * Returns a list of dates for all diary entries for the given month.
     *
     * @param month 1-based (1 = january, 2 = february and so on)
     * @param year
     * @return
     */
    Set<Date> getDiaryEntryDatesByMonth(int month, int year);

    Set<Date> getDiaryEntryDatesByTimeSpan(Date startDate, Date endDate);

    Set<DrugIntakeInterface> getDrugIntakesForDiaryEntry(long diaryEntryID);

    long storeDrug(DrugInterface drug);

    /**
     * Updates the given drug.
     *
     * @param drug drug to update; must be persistent (see {@link DrugInterface#isPersistent()})
     */
    void updateDrug(DrugInterface drug);

    /**
     * Deletes the drug from the database if there are no more references to it.
     *
     * @param drug drug to delete
     */
    void deleteDrug(DrugInterface drug);

    DrugInterface getDrugByID(long id);

    DrugInterface getDrugByNameAndDose(String name, String dose);

    List<DrugInterface> getAllDrugs();

}
