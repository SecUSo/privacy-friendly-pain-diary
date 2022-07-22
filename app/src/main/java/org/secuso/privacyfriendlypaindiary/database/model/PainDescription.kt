package org.secuso.privacyfriendlypaindiary.database.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.secuso.privacyfriendlypaindiary.database.entities.enums.BodyRegion
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.PainDescriptionInterface
import org.secuso.privacyfriendlypaindiary.database.utils.Utils

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
        val painDescription =
            org.secuso.privacyfriendlypaindiary.database.entities.impl.PainDescription(
                painLevel,
                Utils.convertStringToBodyRegionEnumSet(bodyRegions),
                Utils.convertStringToPainQualityEnumSet(painQualities),
                Utils.convertStringToTimeEnumSet(timesOfPain)
            )
        painDescription.objectID = _id
        return painDescription
    }
}