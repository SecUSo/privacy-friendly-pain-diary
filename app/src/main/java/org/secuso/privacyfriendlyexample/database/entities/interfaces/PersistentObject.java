package org.secuso.privacyfriendlyexample.database.entities.interfaces;

/**
 * @author Susanne Felsen
 * @version 20171118
 */
public interface PersistentObject {

    public static final long INVALID_OBJECT_ID = 0L;

    public long getObjectID();

    /**
     * Sets the object's ID. Should not be set unless object is retrieved from the database!
     * @param objectID
     */
    public void setObjectID(long objectID);

    public boolean isPersistent();

}