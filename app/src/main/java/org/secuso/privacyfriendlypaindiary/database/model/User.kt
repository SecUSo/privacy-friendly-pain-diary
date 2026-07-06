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

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Gender
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface
import org.secuso.privacyfriendlypaindiary.database.entities.impl.User as ImplUser
import java.util.Date

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
                userInterface.gender?.value,
                userInterface.dateOfBirth
            )
        }
    }

    fun toUserInterface(): UserInterface {
        val userInterface = ImplUser(
            firstname,
            lastname,
            gender?.let { Gender.valueOf(it) },
            dateOfBirth
        )
        userInterface.objectID = _id
        return userInterface
    }
}