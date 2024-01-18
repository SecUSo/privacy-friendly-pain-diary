package org.secuso.privacyfriendlypaindiary.database

import android.content.Context
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Condition
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugIntakeInterface
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugInterface
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface
import org.secuso.privacyfriendlypaindiary.database.model.DiaryEntry
import org.secuso.privacyfriendlypaindiary.database.model.Drug
import org.secuso.privacyfriendlypaindiary.database.model.DrugIntake
import org.secuso.privacyfriendlypaindiary.database.model.PainDescription
import org.secuso.privacyfriendlypaindiary.database.model.User
import java.util.Calendar
import java.util.Date

class PainDiaryDatabaseService private constructor(context: Context) : DBServiceInterface {
    private var database: PainDiaryDatabase

    companion object {
        const val TAG = "PainDiaryDBService"
        private var instance: PainDiaryDatabaseService? = null

        @Synchronized
        @JvmStatic
        fun getInstance(context: Context): PainDiaryDatabaseService {
            if (instance == null) {
                instance = PainDiaryDatabaseService(context)
            }
            return instance!!
        }
    }

    init {
        database = PainDiaryDatabase.getInstance(context)
    }

    override fun initializeDatabase() {
    }

    override fun reinitializeDatabase(context: Context) {
        PainDiaryDatabase.resetDatabase(context)
        database = PainDiaryDatabase.getInstance(context)
    }

    override fun storeUser(user: UserInterface): Long {
        return database.userDao().insert(User.fromUserInterface(user))
    }

    override fun updateUser(user: UserInterface) {
        database.userDao().update(User.fromUserInterface(user))
    }

    override fun deleteUser(user: UserInterface) {
        database.userDao().deleteUserByID(user.objectID)
    }

    override fun getUserByID(id: Long): UserInterface? {
        return database.userDao().loadUserByID(id)?.toUserInterface() ?: return null
    }

    override fun getAllUsers(): MutableList<UserInterface> {
        return database.userDao().loadAllUsers().map { it.toUserInterface() }.toMutableList()
    }

    override fun storeDiaryEntryAndAssociatedObjects(diaryEntry: DiaryEntryInterface): Long {
        diaryEntry.painDescription.objectID = database.painDescriptionDao()
            .insert(PainDescription.fromPainDescriptionInterface(diaryEntry.painDescription))

        val newDiaryEntry = DiaryEntry.fromDiaryEntryInterface(diaryEntry)
        val newDiaryEntryID = database.diaryEntryDao().insert(newDiaryEntry)

        for (intake: DrugIntakeInterface in diaryEntry.drugIntakes) {
            intake.diaryEntry.objectID = newDiaryEntryID
            storeDrugIntakeAndAssociatedDrug(intake)
        }
        return newDiaryEntryID
    }

    override fun updateDiaryEntryAndAssociatedObjects(diaryEntry: DiaryEntryInterface) {
        val painDescription = diaryEntry.painDescription
        diaryEntry.painDescription.objectID = if (painDescription.isPersistent) {
            database.painDescriptionDao()
                .update(PainDescription.fromPainDescriptionInterface(painDescription))
            painDescription.objectID
        } else {
            database.painDescriptionDao()
                .insert(PainDescription.fromPainDescriptionInterface(painDescription))
        }

        database.diaryEntryDao().update(DiaryEntry.fromDiaryEntryInterface(diaryEntry))

        val oldIntakes: Set<DrugIntakeInterface> = getDrugIntakesForDiaryEntry(diaryEntry.objectID)
        val newIntakes = diaryEntry.drugIntakes
        val newIntakeIDs: MutableSet<Long> = HashSet()
        for (intake in newIntakes) {
            if (!intake.isPersistent) {
                storeDrugIntakeAndAssociatedDrug(intake)
            } else {
                newIntakeIDs.add(intake.objectID)
                updateDrugIntakeAndAssociatedDrug(intake)
            }
        }
        //all drug intake objects that are no longer associated with the diary entry object are deleted
        for (intake in oldIntakes) {
            if (!newIntakeIDs.contains(intake.objectID)) {
                deleteDrugIntake(intake)
            }
        }
    }

    override fun deleteDiaryEntryAndAssociatedObjects(diaryEntry: DiaryEntryInterface) {
        database.painDescriptionDao().deletePainDescriptionByID(diaryEntry.painDescription.objectID)
        for (intake: DrugIntakeInterface in diaryEntry.drugIntakes) {
            deleteDrugIntake(intake)
        }
        database.diaryEntryDao().deleteDiaryEntryByID(diaryEntry.objectID)
    }

    override fun getDiaryEntryByID(id: Long): DiaryEntryInterface? {
        val entry = database.diaryEntryDao().loadDiaryEntryByID(id) ?: return null
        val date = entry.date
        val condition = entry.condition?.let { Condition.valueOf(it) }
        val painDescription =
            database.painDescriptionDao().loadPainDescriptionByID(entry.painDescription_id)
                ?.toPainDescriptionInterface()
        val notes = entry.notes
        val intakes = getDrugIntakesForDiaryEntry(entry._id)

        val diaryEntryInterface =
            org.secuso.privacyfriendlypaindiary.database.entities.impl.DiaryEntry(
                date, condition, painDescription, notes, intakes
            )
        diaryEntryInterface.objectID = entry._id
        return diaryEntryInterface
    }

    override fun getIDOfLatestDiaryEntry(): Long {
        var id = database.diaryEntryDao().getIDOfLatestDiaryEntry()
        id = if (id == 0L) {
            -1
        } else {
            id
        }
        return id
    }

    override fun getDiaryEntryByDate(date: Date): DiaryEntryInterface? {
        val id = database.diaryEntryDao().loadDiaryEntryByDate(date)?._id
        return id?.let { getDiaryEntryByID(it) }
    }

    override fun getDiaryEntriesByMonth(month: Int, year: Int): MutableList<DiaryEntryInterface> {
        val c = Calendar.getInstance()
        c[Calendar.MONTH] = month - 1
        c[Calendar.YEAR] = year
        c[Calendar.DAY_OF_MONTH] = 1
        val startDate = c.time
        c[Calendar.DAY_OF_MONTH] = c.getActualMaximum(Calendar.DAY_OF_MONTH)
        val endDate = c.time
        return getDiaryEntriesByTimeSpan(startDate, endDate)
    }

    override fun getDiaryEntriesByTimeSpan(
        startDate: Date,
        endDate: Date
    ): MutableList<DiaryEntryInterface> {
        val diaryEntries = database.diaryEntryDao().loadDiaryEntriesByDateRange(startDate, endDate)
        val diaryEntryInterfaces: MutableList<DiaryEntryInterface> = ArrayList()
        for (entry: DiaryEntry in diaryEntries) {
            getDiaryEntryByID(entry._id)?.let { diaryEntryInterfaces.add(it) }
        }
        return diaryEntryInterfaces
    }

    override fun getDiaryEntryDatesByMonth(month: Int, year: Int): MutableSet<Date> {
        val c = Calendar.getInstance()
        c[Calendar.MONTH] = month - 1
        c[Calendar.YEAR] = year
        c[Calendar.DAY_OF_MONTH] = 1
        val startDate = c.time
        c[Calendar.DAY_OF_MONTH] = c.getActualMaximum(Calendar.DAY_OF_MONTH)
        val endDate = c.time
        return getDiaryEntryDatesByTimeSpan(startDate, endDate)
    }

    override fun getDiaryEntryDatesByTimeSpan(startDate: Date, endDate: Date): MutableSet<Date> {
        val dateSet: MutableSet<Date> = HashSet()
        val dates = database.diaryEntryDao().getDatesByDateRange(startDate, endDate)
        for (date: Date in dates) {
            dateSet.add(date)
        }
        return dateSet
    }

    override fun getDrugIntakesForDiaryEntry(diaryEntryID: Long): MutableSet<DrugIntakeInterface> {
        val intakes: MutableSet<DrugIntakeInterface> = HashSet()

        val drugIntakes = database.drugIntakeDao().loadDrugIntakesByDiaryEntryID(diaryEntryID)

        for (intake in drugIntakes) {
            getDrugIntakeByID(intake._id)?.let { intakes.add(it) }
        }
        return intakes
    }

    override fun storeDrug(drug: DrugInterface): Long {
        return database.drugDao().insert(Drug(drug.name, drug.dose))
    }

    override fun updateDrug(drug: DrugInterface) {
        database.drugDao().update(Drug.fromDrugInterface(drug))
    }

    override fun deleteDrug(drug: DrugInterface) {
        val drugIntakes = database.drugIntakeDao().loadDrugIntakesByDrugID(drug.objectID)
        if (drugIntakes.isEmpty()) { //check if drug is used anywhere before deletion
            database.drugDao().deleteDrugByID(drug.objectID)
        }
    }

    override fun getDrugByID(id: Long): DrugInterface? {
        return database.drugDao().loadDrugByID(id)?.toDrugInterface()
    }

    override fun getDrugByNameAndDose(name: String, dose: String?): DrugInterface? {
        val drug: Drug? = if (dose == null) {
            database.drugDao().loadDrugWithoutDoseByName(name)
        } else {
            database.drugDao().loadDrugByNameAndDose(name, dose)
        }
        if (drug == null) {
            return null
        }
        val drugInterface: DrugInterface =
            org.secuso.privacyfriendlypaindiary.database.entities.impl.Drug(drug.name, drug.dose)
        drugInterface.objectID = drug._id
        return drugInterface
    }

    override fun getAllDrugs(): MutableList<DrugInterface> {
        return database.drugDao().loadAllDrugs().map { it.toDrugInterface() }.toMutableList()
    }


    private fun storeDrugIntakeAndAssociatedDrug(intake: DrugIntakeInterface): Long {
        val drug = intake.drug
        val drugFromDatabase = getDrugByNameAndDose(drug.name, drug.dose)
        val drugID: Long = drugFromDatabase?.objectID ?: storeDrug(drug)
        return database.drugIntakeDao().insert(
            DrugIntake(
                intake.quantityMorning,
                intake.quantityNoon,
                intake.quantityEvening,
                intake.quantityNight,
                drugID,
                intake.diaryEntry.objectID
            )
        )
    }

    private fun updateDrugIntakeAndAssociatedDrug(intake: DrugIntakeInterface) {
        val oldIntake = getDrugIntakeByID(intake.objectID)
        var drugID: Long = intake.drug.objectID
        if (oldIntake == null || !oldIntake.drug.equals(intake.drug)) {
            val drugFromDatabase = getDrugByNameAndDose(intake.drug.name, intake.drug.dose)
            drugID = drugFromDatabase?.objectID ?: storeDrug(intake.drug)
        }
        database.drugIntakeDao().update(
            DrugIntake(
                intake.objectID,
                intake.quantityMorning,
                intake.quantityNoon,
                intake.quantityEvening,
                intake.quantityNight,
                drugID,
                intake.diaryEntry.objectID
            )
        )
        if (oldIntake != null && drugID != intake.drug.objectID) { //drug changed
            deleteDrug(oldIntake.drug)
        }
    }

    private fun deleteDrugIntake(intake: DrugIntakeInterface) {
        val drug = intake.drug
        database.drugIntakeDao().deleteDrugIntakeByID(intake.objectID)
        if (drug != null && drug.isPersistent) {
            deleteDrug(drug)
        }
    }

    private fun getDrugIntakeByID(id: Long): DrugIntakeInterface? {
        val drugIntake = database.drugIntakeDao().loadDrugIntakeByID(id) ?: return null
        val drug = database.drugDao().loadDrugByID(drugIntake.drug_id) ?: return null
        val drugInterface =
            org.secuso.privacyfriendlypaindiary.database.entities.impl.Drug(drug.name, drug.dose)
        drugInterface.objectID = drug._id
        val drugIntakeInterface =
            org.secuso.privacyfriendlypaindiary.database.entities.impl.DrugIntake(
                drugInterface,
                drugIntake.morning, drugIntake.noon, drugIntake.evening, drugIntake.night
            )
        drugIntakeInterface.objectID = drugIntake._id
        return drugIntakeInterface
    }
}