package org.secuso.privacyfriendlypaindiary.database.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface
import java.util.Date

@Entity(tableName = "diaryentries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true)
    var _id: Long = 0,
    var date: Date,
    var painDescription_id: Long,
    var condition: Int?,
    var notes: String?
) {
    @Ignore
    constructor(
        date: Date,
        painDescription_id: Long,
        condition: Int?,
        notes: String?
    ) : this(
        date = date,
        condition = condition,
        notes = notes,
        painDescription_id = painDescription_id,
        _id = 0
    )

    companion object {
        @JvmStatic
        fun fromDiaryEntryInterface(diaryEntryInterface: DiaryEntryInterface): DiaryEntry {
            return DiaryEntry(
                diaryEntryInterface.objectID,
                diaryEntryInterface.date,
                diaryEntryInterface.painDescription.objectID,
                diaryEntryInterface.condition?.value,
                diaryEntryInterface.notes
            )
        }
    }
}