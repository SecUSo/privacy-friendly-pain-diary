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
package org.secuso.privacyfriendlypaindiary.database.entities.interfaces;

/**
 * @author Susanne Felsen
 * @version 20171124
 */
public interface DrugInterface extends PersistentObject {

    public String getName();
    public void setName(String name);
    public String getDose();
    public void setDose(String dose);
    public boolean isCurrentlyTaken();
    public void setCurrentlyTaken(boolean currentlyTaken);

}
