package org.secuso.privacyfriendlyexample.database.entities.interfaces;

/**
 * @author Susanne Felsen
 * @version 20171124
 */
public interface DrugIntakeInterface {

    public DiaryEntryInterface getDiaryEntry();
    public void setDiaryEntry(DiaryEntryInterface diaryEntry);
    public DrugInterface getDrug();
    public void setDrug(DrugInterface drug);
    public int getQuantityMorning();
    public void setQuantityMorning(int quantityMorning);
    public int getQuantityNoon();
    public void setQuantityNoon(int quantityNoon);
    public int getQuantityEvening();
    public void setQuantityEvening(int quantityEvening);
    public int getQuantityNight();
    public void setQuantityNight(int quantityNight);

}
