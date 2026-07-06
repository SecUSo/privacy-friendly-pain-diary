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
import org.secuso.privacyfriendlypaindiary.database.model.Drug

@Dao
interface DrugDao {
    @Insert
    fun insert(drug: Drug): Long

    @Update
    fun update(drug: Drug)

    @Delete
    fun delete(drug: Drug)

    @Query("SELECT * FROM drugs WHERE name = :name AND dose IS NULL")
    fun loadDrugWithoutDoseByName(name: String): Drug?

    @Query("SELECT * FROM drugs WHERE name = :name AND dose = :dose")
    fun loadDrugByNameAndDose(name: String, dose: String): Drug?

    @Query("SELECT * FROM drugs")
    fun loadAllDrugs(): Array<Drug>

    @Query("SELECT * FROM drugs WHERE _id = :id")
    fun loadDrugByID(id: Long): Drug?

    @Query("DELETE FROM drugs WHERE _id = :id")
    fun deleteDrugByID(id: Long)
}