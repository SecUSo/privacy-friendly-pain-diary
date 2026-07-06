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
import org.secuso.privacyfriendlypaindiary.database.model.DiaryEntry
import java.util.*

@Dao
interface DiaryEntryDao {
    @Insert
    fun insert(diaryEntry: DiaryEntry): Long

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
    fun getIDOfLatestDiaryEntry(): Long

    @Query("SELECT date FROM diaryentries WHERE date >= :startDate AND date <= :endDate ORDER BY DATE(date) asc")
    fun getDatesByDateRange(startDate: Date, endDate: Date): Array<Date>

}