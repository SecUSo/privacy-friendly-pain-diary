package org.secuso.privacyfriendlypaindiary.database.entities.interfaces;

import org.secuso.privacyfriendlypaindiary.database.entities.enums.Condition;

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

    /**
     * Adds a drug intake to the set of drug intakes associated with this entry.
     * The set must not contain another drug intake associated with the same drug.
     *
     * @param intake drug intake to be added
     */
    public void addDrugIntake(DrugIntakeInterface intake);

    public void removeDrugIntake(DrugIntakeInterface intake);

    /**
     * @param id
     * @return drug intake object or <code>null</code>
     */
    public DrugIntakeInterface getDrugIntakeByID(long id);

}
