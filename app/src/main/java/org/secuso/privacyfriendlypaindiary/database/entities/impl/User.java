package org.secuso.privacyfriendlypaindiary.database.entities.impl;

import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Gender;

import java.util.Date;

/**
 * @author Susanne Felsen
 * @version 20171118
 */
public class User extends AbstractPersistentObject implements UserInterface {

    public static final String TABLE_NAME = "user";
    public static final String COLUMN_FIRST_NAME = "firstname";
    public static final String COLUMN_LAST_NAME = "lastname";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_DATE_OF_BIRTH = "dateofbirth";

    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FIRST_NAME + " TEXT, " +
            COLUMN_LAST_NAME + " TEXT, " +
            COLUMN_GENDER + " INTEGER, " +
//            COLUMN_GENDER + " INTEGER REFERENCES GENDER(Type), " +
            COLUMN_DATE_OF_BIRTH + " TEXT);";

    private String firstName;
    private String lastName;
    private Gender gender;
    private Date dateOfBirth;

    public User(String firstName, String lastName, Gender gender, Date dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    @Override
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

}
