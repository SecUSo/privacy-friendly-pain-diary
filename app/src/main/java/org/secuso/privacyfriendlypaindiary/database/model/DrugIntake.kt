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
package org.secuso.privacyfriendlypaindiary.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "drugintakes")
data class DrugIntake(
    @PrimaryKey(autoGenerate = true)
    var _id: Long = 0,
    @ColumnInfo(defaultValue = "0") var morning: Int,
    @ColumnInfo(defaultValue = "0") var noon: Int,
    @ColumnInfo(defaultValue = "0") var evening: Int,
    @ColumnInfo(defaultValue = "0") var night: Int,
    var drug_id: Long,
    var diaryEntry_id: Long
) {
    @Ignore
    constructor(
        morning: Int,
        noon: Int,
        evening: Int,
        night: Int,
        drug_id: Long,
        diaryEntry_id: Long
    ) : this(
        morning = morning,
        noon = noon,
        evening = evening,
        night = night,
        drug_id = drug_id,
        diaryEntry_id = diaryEntry_id,
        _id = 0
    )
}