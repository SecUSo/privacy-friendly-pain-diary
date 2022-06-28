package org.secuso.privacyfriendlypaindiary.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import org.secuso.privacyfriendlypaindiary.database.model.DiaryEntry

@Dao
interface DiaryEntryDao {
    @Insert
    fun insert(diaryEntry: DiaryEntry)

    @Update
    fun update(diaryEntry: DiaryEntry)

    @Delete
    fun delete(diaryEntry: DiaryEntry)
}