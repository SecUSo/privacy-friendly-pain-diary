package org.secuso.privacyfriendlyexample.database.entities.interfaces;

import org.secuso.privacyfriendlyexample.database.entities.enums.BodyRegion;
import org.secuso.privacyfriendlyexample.database.entities.enums.Condition;
import org.secuso.privacyfriendlyexample.database.entities.impl.PainDescription;

import java.util.Date;
import java.util.Set;

/**
 * @author Susanne Felsen
 * @version 20171124
 */
public interface DiaryEntryInterface extends PersistentObject {

    public Date getDate();
    public void setDate(Date date);
    public Condition getCondition();
    public void setCondition(Condition condition);
    public PainDescriptionInterface getPainDescription();
    public void setPainDescription(PainDescriptionInterface painDescription);
    public String getNotes();
    public void setNotes(String notes);
    public Set<DrugIntakeInterface> getDrugIntakes();
    public void addDrugIntake(DrugIntakeInterface intake);
    public void removeDrugIntake(DrugIntakeInterface intake);

}
