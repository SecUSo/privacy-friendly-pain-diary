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
package org.secuso.privacyfriendlypaindiary.database.dao

import androidx.room.*
import org.secuso.privacyfriendlypaindiary.database.model.DrugIntake

@Dao
interface DrugIntakeDao {
    @Insert
    fun insert(drugIntake: DrugIntake): Long

    @Update
    fun update(drugIntake: DrugIntake)

    @Delete
    fun delete(drugIntake: DrugIntake)

    @Query("SELECT * FROM drugintakes WHERE _id = :id")
    fun loadDrugIntakeByID(id: Long): DrugIntake?

    @Query("DELETE FROM drugintakes WHERE _id = :id")
    fun deleteDrugIntakeByID(id: Long)

    @Query("SELECT * FROM drugintakes WHERE drug_id = :drugID")
    fun loadDrugIntakesByDrugID(drugID: Long): Array<DrugIntake>

    @Query("SELECT * FROM drugintakes WHERE diaryEntry_id = :diaryEntryID")
    fun loadDrugIntakesByDiaryEntryID(diaryEntryID: Long): Array<DrugIntake>
}