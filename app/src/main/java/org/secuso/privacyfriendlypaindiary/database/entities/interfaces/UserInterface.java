package org.secuso.privacyfriendlypaindiary.database.entities.interfaces;

import org.secuso.privacyfriendlypaindiary.database.entities.enums.Gender;

import java.util.Date;

/**
 * @author Susanne Felsen
 * @version 20171118
 */
public interface UserInterface extends PersistentObject {

    public String getFirstName();
    public void setFirstName(String firstName);
    public String getLastName();
    public void setLastName(String lastName);
    public Gender getGender();
    public void setGender(Gender gender);
    public Date getDateOfBirth();
    public void setDateOfBirth(Date dateOfBirth);

}
