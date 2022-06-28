package org.secuso.privacyfriendlypaindiary.database.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "diaryentries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true)
    var _id: Long = 0,
    var date: String,
    var painDescription_id: Long,
    var condition: Int,
    var notes: String?
) {
    @Ignore
    constructor(
        date: String,
        condition: Int,
        notes: String?
    ) : this(
        date = date,
        condition = condition,
        notes = notes,
        painDescription_id = 0,
        _id = 0
    )
}