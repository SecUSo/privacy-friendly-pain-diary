package org.secuso.privacyfriendlyexample.database.entities.interfaces;

/**
 * @author Susanne Felsen
 * @version 20171118
 */
public interface PersistentObject {

    public static final long INVALID_OBJECT_ID = 0L;

    public long getObjectID();

    public boolean isPersistent();

}