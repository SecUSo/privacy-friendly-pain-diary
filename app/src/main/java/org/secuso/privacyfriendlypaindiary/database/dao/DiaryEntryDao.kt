package org.secuso.privacyfriendlypaindiary.database.dao

import androidx.room.*
import org.secuso.privacyfriendlypaindiary.database.model.DiaryEntry
import org.secuso.privacyfriendlypaindiary.database.model.Drug
import java.util.*

@Dao
interface DiaryEntryDao {
    @Insert
    fun insert(diaryEntry: DiaryEntry) : Long

    @Update
    fun update(diaryEntry: DiaryEntry)

    @Delete
    fun delete(diaryEntry: DiaryEntry)

    @Query("SELECT * FROM diaryentries WHERE _id = :id")
    fun loadDiaryEntryByID(id: Long): DiaryEntry?

    @Query("SELECT * FROM diaryentries WHERE date = :date")
    fun loadDiaryEntryByDate(date: Date): DiaryEntry?

    @Query("SELECT * FROM diaryentries WHERE date >= :startDate AND date <= :endDate ORDER BY DATE(date) asc")
    fun loadDiaryEntriesByDateRange(startDate: Date, endDate: Date): Array<DiaryEntry>

    @Query("DELETE FROM diaryentries WHERE _id = :id")
    fun deleteDiaryEntryByID(id: Long)

    @Query("SELECT MAX(_id) FROM diaryentries")
    fun getIDOfLatestDiaryEntry() : Long

    @Query("SELECT date FROM diaryentries WHERE date >= :startDate AND date <= :endDate ORDER BY DATE(date) asc")
    fun getDatesByDateRange(startDate: Date, endDate: Date): Array<Date>

}