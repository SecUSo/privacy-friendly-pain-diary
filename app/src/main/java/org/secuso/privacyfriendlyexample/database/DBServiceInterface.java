package org.secuso.privacyfriendlyexample.database;

import android.database.sqlite.SQLiteDatabase;

import org.secuso.privacyfriendlyexample.database.entities.enums.Gender;
import org.secuso.privacyfriendlyexample.database.entities.impl.User;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.DrugInterface;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.UserInterface;

import java.util.Date;

/**
 * @author Susanne Felsen
 * @version 20171121
 */
public interface DBServiceInterface {

    public long createAndStoreUser(String firstName, String lastName, Gender gender, Date dateOfBirth);

    public void updateUser(UserInterface user);

    public UserInterface getUserByID(long id);

    public void deleteUser(UserInterface user);

    public long createAndStoreDrug(String name, String dose);

    public long createAndStoreDrug(DrugInterface drug);

    public long storeDiaryEntryAndAssociatedObjects(DiaryEntryInterface diaryEntry);

}
