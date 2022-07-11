package org.secuso.privacyfriendlypaindiary.database.dao

import androidx.room.*
import org.secuso.privacyfriendlypaindiary.database.model.DiaryEntry
import org.secuso.privacyfriendlypaindiary.database.model.PainDescription

@Dao
interface PainDescriptionDao {
    @Insert
    fun insert(painDescription: PainDescription) : Long

    @Update
    fun update(painDescription: PainDescription)

    @Delete
    fun delete(painDescription: PainDescription)

    @Query("SELECT * FROM paindescriptions WHERE _id = :id")
    fun loadPainDescriptionByID(id: Long): PainDescription?

    @Query("DELETE FROM paindescriptions WHERE _id = :id")
    fun deletePainDescriptionByID(id: Long)
}