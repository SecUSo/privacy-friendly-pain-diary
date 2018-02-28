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

import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Gender;

import java.util.Date;

/**
 * An instances of this class represents a user, i.e. a person that is using the app.
 * A user is characterized by his first and last name, date of birth and gender.
 * However, none of that information is required (fields might be <code>null</code>).
 *
 * @author Susanne Felsen
 * @version 20171118
 */
public class User extends AbstractPersistentObject implements UserInterface {

    public static final String TABLE_NAME = "user";
    public static final String COLUMN_FIRST_NAME = "firstname";
    public static final String COLUMN_LAST_NAME = "lastname";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_DATE_OF_BIRTH = "dateofbirth";

    public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FIRST_NAME + " TEXT, " +
            COLUMN_LAST_NAME + " TEXT, " +
            COLUMN_GENDER + " INTEGER, " +
            COLUMN_DATE_OF_BIRTH + " TEXT);";

    private String firstName;
    private String lastName;
    private Gender gender;
    private Date dateOfBirth;

    public User() {
    }

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
