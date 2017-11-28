package org.secuso.privacyfriendlypaindiary.database;

import org.secuso.privacyfriendlypaindiary.database.entities.enums.Gender;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugIntakeInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface;

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
     *
     * @param user user to update; must be persistent (see {@link UserInterface#isPersistent()})
     */
    public void updateUser(UserInterface user);

    public UserInterface getUserByID(long id);

    public void deleteUser(UserInterface user);

    /**
     * Stores the given drug intake and the associated drug (if not yet persistent).
     *
     * @param intake intake to be stored; associated diary entry object has to be persistent (objectID must be set)
     * @return
     */
    public long storeDrugIntakeAndAssociatedDrug(DrugIntakeInterface intake);

    /**
     * Updates the given drug intake. Associated drug can not be updated (call {@link DBServiceInterface#deleteDrugIntake(DrugIntakeInterface)}
     * and {@link DBServiceInterface#storeDrugIntakeAndAssociatedDrug(DrugIntakeInterface)} instead.)
     *
     * @param intake drug intake to update; must be persistent (see {@link DrugIntakeInterface#isPersistent()})
     */
    public void updateDrugIntake(DrugIntakeInterface intake);

    /**
     * Deletes the given drug intake object and calls {@link DBServiceInterface#deleteDrug(DrugInterface)} with the associated drug.
     *
     * @param intake drug intake to delete
     */
    public void deleteDrugIntake(DrugIntakeInterface intake);

    public long createAndStoreDrug(String name, String dose);

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

    public long storeDiaryEntryAndAssociatedObjects(DiaryEntryInterface diaryEntry);

    public List<DiaryEntryInterface> getDiaryEntriesByDate(Date date);

}
