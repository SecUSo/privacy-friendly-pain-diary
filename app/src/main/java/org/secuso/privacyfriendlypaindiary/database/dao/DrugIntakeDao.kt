package org.secuso.privacyfriendlypaindiary.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import org.secuso.privacyfriendlypaindiary.database.model.DrugIntake

@Dao
interface DrugIntakeDao {
    @Insert
    fun insert(drugIntake: DrugIntake)

    @Update
    fun update(drugIntake: DrugIntake)

    @Delete
    fun delete(drugIntake: DrugIntake)
}