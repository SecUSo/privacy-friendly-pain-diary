package org.secuso.privacyfriendlypaindiary.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import org.secuso.privacyfriendlypaindiary.database.model.PainDescription

@Dao
interface PainDescriptionDao {
    @Insert
    fun insert(painDescription: PainDescription)

    @Update
    fun update(painDescription: PainDescription)

    @Delete
    fun delete(painDescription: PainDescription)
}