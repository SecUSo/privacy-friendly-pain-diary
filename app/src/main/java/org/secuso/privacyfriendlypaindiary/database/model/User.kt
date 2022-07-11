package org.secuso.privacyfriendlypaindiary.database.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Gender
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface
import java.util.*

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    var _id: Long = 0,
    var firstname: String?,
    var lastname: String?,
    var gender: Int?,
    var dateOfBirth: Date?
) {
    @Ignore
    constructor(firstname: String?, lastname: String?, gender: Int?, dateOfBirth: Date?) : this(
        firstname = firstname,
        lastname = lastname,
        gender = gender,
        dateOfBirth = dateOfBirth,
        _id = 0
    )

    companion object {
        @JvmStatic
        fun fromUserInterface(userInterface: UserInterface): User {
            return User(
                userInterface.objectID,
                userInterface.firstName,
                userInterface.lastName,
                userInterface.gender.value,
                userInterface.dateOfBirth
            )
        }
    }

    fun toUserInterface(): UserInterface {
        val userInterface = org.secuso.privacyfriendlypaindiary.database.entities.impl.User(
            firstname,
            lastname,
            gender?.let { Gender.valueOf(it) },
            dateOfBirth
        )
        userInterface.objectID = _id
        return userInterface
    }
}