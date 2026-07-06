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
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugInterface
import org.secuso.privacyfriendlypaindiary.database.entities.impl.Drug as ImplDrug
@Entity(tableName = "drugs")
data class Drug(
    @PrimaryKey(autoGenerate = true)
    var _id: Long = 0,
    var name: String,
    var dose: String?
) {
    @Ignore
    constructor(name: String, dose: String?) : this(name = name, dose = dose, _id = 0)

    companion object {
        @JvmStatic
        fun fromDrugInterface(drugInterface: DrugInterface): Drug {
            return Drug(
                drugInterface.objectID,
                drugInterface.name,
                drugInterface.dose
            )
        }
    }

    fun toDrugInterface(): DrugInterface {
        val drugInterface = ImplDrug(
            name,
            dose
        )
        drugInterface.objectID = _id
        return drugInterface
    }
}