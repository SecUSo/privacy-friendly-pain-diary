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