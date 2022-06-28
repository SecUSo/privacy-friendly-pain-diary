package org.secuso.privacyfriendlypaindiary.database.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    var _id: Long = 0,
    var firstname: String?,
    var lastname: String?,
    var gender: Int?,
    var dateOfBirth: String?
) {
    @Ignore
    constructor(firstname: String?, lastname: String?, gender: Int?, dateOfBirth: String?) : this(
        firstname = firstname,
        lastname = lastname,
        gender = gender,
        dateOfBirth = dateOfBirth,
        _id = 0
    )
}