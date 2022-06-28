package org.secuso.privacyfriendlypaindiary.database.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "drugs")
data class Drug(
    @PrimaryKey(autoGenerate = true)
    var _id: Long = 0,
    var name: String,
    var dose: String?
) {
    @Ignore
    constructor(name: String, dose: String?) : this(name = name, dose = dose, _id = 0)
}