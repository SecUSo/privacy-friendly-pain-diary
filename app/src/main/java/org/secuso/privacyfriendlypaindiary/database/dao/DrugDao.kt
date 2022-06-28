package org.secuso.privacyfriendlypaindiary.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import org.secuso.privacyfriendlypaindiary.database.model.Drug

@Dao
interface DrugDao {
    @Insert
    fun insert(drug: Drug)

    @Update
    fun update(drug: Drug)

    @Delete
    fun delete(drug: Drug)
}