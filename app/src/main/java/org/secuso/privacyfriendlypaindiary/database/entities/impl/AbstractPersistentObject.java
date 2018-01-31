/*
    This file is part of Privacy Friendly Pain Diary.

    Privacy Friendly Pain Diary is free software: you can redistribute it
    and/or modify it under the terms of the GNU General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
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