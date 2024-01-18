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