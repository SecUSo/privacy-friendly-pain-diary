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

import org.secuso.privacyfriendlypaindiary.database.entities.enums.Gender;

import java.util.Date;

/**
 * User, i.e. person that is using the app, characterized by his first and last name,
 * date of birth and gender.
 * <p/>
 * Since users are not required to enter any information, the methods might return
 * <code>null</code>.
 *
 * @author Susanne Felsen
 * @version 20171118
 */
public interface UserInterface extends PersistentObject {

    String getFirstName();

    void setFirstName(String firstName);

    String getLastName();

    void setLastName(String lastName);

    Gender getGender();

    void setGender(Gender gender);

    Date getDateOfBirth();

    void setDateOfBirth(Date dateOfBirth);

}
