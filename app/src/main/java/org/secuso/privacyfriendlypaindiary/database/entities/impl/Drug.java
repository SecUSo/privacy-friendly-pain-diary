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

import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugInterface;

/**
 * Instances of this class represent pharmaceutical drugs. A drug is
 * characterized by its name and dose. There should only be one drug for each
 * combination of name and dose in the database.
 *
 * @author Susanne Felsen
 * @version 20180228
 */
public class Drug extends AbstractPersistentObject implements DrugInterface {

    public static final String TABLE_NAME = "drug";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DOSE = "dose";

    public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_DOSE + " TEXT);";

    private String name;
    private String dose;

    public Drug(String name, String dose) {
        this.name = name;
        this.dose = dose;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDose() {
        return dose;
    }

    @Override
    public void setDose(String dose) {
        this.dose = dose;
    }

    @Override
    public int hashCode() {
//        int result = super.hashCode();
        int result = 1;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (dose != null ? dose.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
//        if (!super.equals(o)) return false;

        Drug drug = (Drug) o;

        if (name != null ? !name.equals(drug.name) : drug.name != null) return false;
        return dose != null ? dose.equals(drug.dose) : drug.dose == null;
    }

}
