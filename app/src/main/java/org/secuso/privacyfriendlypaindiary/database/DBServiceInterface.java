package org.secuso.privacyfriendlypaindiary.database;

import org.secuso.privacyfriendlypaindiary.database.entities.enums.Gender;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugIntakeInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.PainDescriptionInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface;

import java.util.Date;
import java.util.List;

/**
 * @author Susanne Felsen
 * @version 20171121
 */
public interface DBServiceInterface {

    public long storeUser(UserInterface user);

    /**
     * Updates the given user.
     *
     * @param user user to update; must be persistent (see {@link UserInterface#isPersistent()})
     */
    public void updateUser(UserInterface user);

    public void deleteUser(UserInterface user);

    public UserInterface getUserByID(long id);

    public long storeDiaryEntryAndAssociatedObjects(DiaryEntryInterface diaryEntry);

    /**
     * Updates the given diary entry and associated pain description and drug intakes.
     *
     * @param diaryEntry diary entry to update; must be persistent, as does the pain description object
     */
    public void updateDiaryEntryAndAssociatedObjects(DiaryEntryInterface diaryEntry);

    public void deleteDiaryEntryAndAssociatedObjects(DiaryEntryInterface diaryEntry);

    public DiaryEntryInterface getDiaryEntryByID(long id);

    public List<DiaryEntryInterface> getDiaryEntriesByDate(Date date);

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

    public List<DrugInterface> getAllDrugs();

    public List<DrugInterface> getAllCurrentlyTakenDrugs();

}
