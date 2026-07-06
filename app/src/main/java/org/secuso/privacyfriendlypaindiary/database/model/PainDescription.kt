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
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.PainDescriptionInterface
import org.secuso.privacyfriendlypaindiary.database.utils.Utils
import org.secuso.privacyfriendlypaindiary.database.entities.impl.PainDescription as ImplPainDescription
@Entity(tableName = "paindescriptions")
data class PainDescription(
    @PrimaryKey(autoGenerate = true)
    var _id: Long = 0,
    var painLevel: Int,
    var bodyRegions: String?,
    var painQualities: String?,
    var timesOfPain: String?
) {
    @Ignore
    constructor(
        painLevel: Int,
        bodyRegions: String?,
        painQualities: String?,
        timesOfPain: String?
    ) : this(
        painLevel = painLevel,
        bodyRegions = bodyRegions,
        painQualities = painQualities,
        timesOfPain = timesOfPain,
        _id = 0
    )

    companion object {
        @JvmStatic
        fun fromPainDescriptionInterface(painDescriptionInterface: PainDescriptionInterface): PainDescription {
            return PainDescription(
                painDescriptionInterface.objectID,
                painDescriptionInterface.painLevel,
                Utils.convertBodyRegionEnumSetToString(painDescriptionInterface.bodyRegions),
                Utils.convertPainQualityEnumSetToString(painDescriptionInterface.painQualities),
                Utils.convertTimeEnumSetToString(painDescriptionInterface.timesOfPain)
            )
        }
    }

    fun toPainDescriptionInterface(): PainDescriptionInterface {
        val painDescription = ImplPainDescription(
                painLevel,
                Utils.convertStringToBodyRegionEnumSet(bodyRegions),
                Utils.convertStringToPainQualityEnumSet(painQualities),
                Utils.convertStringToTimeEnumSet(timesOfPain)
            )
        painDescription.objectID = _id
        return painDescription
    }
}