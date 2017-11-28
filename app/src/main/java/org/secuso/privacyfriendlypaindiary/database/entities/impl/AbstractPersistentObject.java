package org.secuso.privacyfriendlypaindiary.database.entities.impl;

import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.PersistentObject;

/**
 * @author Susanne Felsen
 * @version 20171118
 */
public abstract class AbstractPersistentObject implements PersistentObject {

    public static final String COLUMN_ID = "id";

    protected Long objectID = INVALID_OBJECT_ID;

    @Override
    public long getObjectID() {
        return objectID;
    }

    @Override
    public void setObjectID(long objectID) {
        this.objectID = objectID;
    }

    @Override
    public boolean isPersistent() {
        if (objectID > 0L && objectID != INVALID_OBJECT_ID) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return objectID != null ? objectID.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractPersistentObject that = (AbstractPersistentObject) o;

        return objectID != null ? objectID.equals(that.objectID) : that.objectID == null;
    }

}