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
 * A drug intake encapsulates information about the quantity in which a
 * specific drug (represented by the associated {@link DrugInterface} object)
 * is taken in by a user at different times of day. Every drug intake is
 * associated with a diary entry.
 *
 * @author Susanne Felsen
 * @version 20171124
 */
public interface DrugIntakeInterface extends PersistentObject {

    DiaryEntryInterface getDiaryEntry();

    void setDiaryEntry(DiaryEntryInterface diaryEntry);

    DrugInterface getDrug();

    int getQuantityMorning();

    void setQuantityMorning(int quantityMorning);

    int getQuantityNoon();

    void setQuantityNoon(int quantityNoon);

    int getQuantityEvening();

    void setQuantityEvening(int quantityEvening);

    int getQuantityNight();

    void setQuantityNight(int quantityNight);

}
