package org.secuso.privacyfriendlyexample.database.entities.interfaces;

/**
 * @author Susanne Felsen
 * @version 20171118
 */
public interface DrugInterface extends PersistentObject {

    public String getName();
    public void setName(String name);
    public String getDose();
    public void setDose(String dose);
    public boolean isCurrentlyTaken();
    public void setCurrentlyTaken(boolean currentlyTaken);
    public int getQuantityMorning();
    public void setQuantityMorning(int quantityMorning);
    public int getQuantityNoon();
    public void setQuantityNoon(int quantityNoon);
    public int getQuantityEvening();
    public void setQuantityEvening(int quantityEvening);
    public int getQuantityNight();
    public void setQuantityNight(int quantityNight);

}
