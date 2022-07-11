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