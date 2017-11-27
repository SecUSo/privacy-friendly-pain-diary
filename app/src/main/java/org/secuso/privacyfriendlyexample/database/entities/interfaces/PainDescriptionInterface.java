package org.secuso.privacyfriendlyexample.database.entities.interfaces;

import org.secuso.privacyfriendlyexample.database.entities.enums.BodyRegion;

/**
 * @author Susanne Felsen
 * @version 20171118
 */
public interface PainDescriptionInterface extends PersistentObject {

    public int getPainLevel();
    public void setPainLevel(int painLevel);
    public BodyRegion getBodyRegion();
    public void setBodyRegion(BodyRegion bodyRegion);

}
