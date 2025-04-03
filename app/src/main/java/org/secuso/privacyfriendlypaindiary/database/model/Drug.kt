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