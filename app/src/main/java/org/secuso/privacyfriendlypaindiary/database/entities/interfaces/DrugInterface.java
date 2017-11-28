package org.secuso.privacyfriendlypaindiary.database.entities.interfaces;

/**
 * @author Susanne Felsen
 * @version 20171124
 */
public interface DrugInterface extends PersistentObject {

    public String getName();
    public void setName(String name);
    public String getDose();
    public void setDose(String dose);
    public boolean isCurrentlyTaken();
    public void setCurrentlyTaken(boolean currentlyTaken);

}
