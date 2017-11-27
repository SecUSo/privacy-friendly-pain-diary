package org.secuso.privacyfriendlyexample.database;

import android.database.sqlite.SQLiteDatabase;

import org.secuso.privacyfriendlyexample.database.entities.enums.Gender;
import org.secuso.privacyfriendlyexample.database.entities.impl.User;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.DrugIntakeInterface;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.DrugInterface;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.UserInterface;

import java.util.Date;
import java.util.List;

/**
 * @author Susanne Felsen
 * @version 20171121
 */
public interface DBServiceInterface {

    public long createAndStoreUser(String firstName, String lastName, Gender gender, Date dateOfBirth);

    /**
     * Updates the given user.
     * @param user user to update; has to be persistent (see {@link UserInterface#isPersistent()})
     */
    public void updateUser(UserInterface user);

    public UserInterface getUserByID(long id);

    public void deleteUser(UserInterface user);

    /**
     * Stores the given drug intake and the associated drug (if not yet persistent).
     * @param intake intake to be stored; associated diary entry object has to be persistent (objectID must be set)
     * @return
     */
    public long storeDrugIntakeAndAssociatedDrug(DrugIntakeInterface intake);

    public long createAndStoreDrug(String name, String dose);

    public long storeDrug(DrugInterface drug);

    /**
     * Updates the given drug.
     * @param drug drug to update; has to be persistent (see {@link DrugInterface#isPersistent()})
     */
    public void updateDrug(DrugInterface drug);

    public DrugInterface getDrugByID(long id);

    public List<DrugInterface> getAllDrugs();

    public List<DrugInterface> getAllCurrentlyTakenDrugs();

    public long storeDiaryEntryAndAssociatedObjects(DiaryEntryInterface diaryEntry);

    public List<DiaryEntryInterface> getDiaryEntriesByDate(Date date);

}
