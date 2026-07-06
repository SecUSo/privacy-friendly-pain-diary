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
package org.secuso.privacyfriendlypaindiary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.secuso.privacyfriendlypaindiary.database.PainDiaryDatabaseService
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugIntakeInterface
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugInterface
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface
import java.util.Date

class DatabaseViewModel(application: Application) : AndroidViewModel(application) {
    val service = PainDiaryDatabaseService.getInstance(application)

    fun storeDiaryEntryAndAssociatedObjects(diaryEntry: DiaryEntryInterface) {
        viewModelScope.launch(Dispatchers.IO) {
            service.storeDiaryEntryAndAssociatedObjects(
                diaryEntry
            )
        }
    }

    fun updateDiaryEntryAndAssociatedObjects(diaryEntry: DiaryEntryInterface) {
        viewModelScope.launch(Dispatchers.IO) {
            service.updateDiaryEntryAndAssociatedObjects(
                diaryEntry
            )
        }
    }

    fun getDiaryEntryByDate(date: Date): LiveData<DiaryEntryInterface> {
        val diaryEntry = MutableLiveData<DiaryEntryInterface>()
        viewModelScope.launch(Dispatchers.IO) {
            diaryEntry.postValue(service.getDiaryEntryByDate(date))
        }
        return diaryEntry
    }

    fun getDiaryEntryDatesByTimeSpan(startDate: Date, endDate: Date): LiveData<MutableSet<Date>> {
        val dateValues = MutableLiveData<MutableSet<Date>>()
        viewModelScope.launch(Dispatchers.IO) {
            dateValues.postValue(service.getDiaryEntryDatesByTimeSpan(startDate, endDate))
        }
        return dateValues
    }

    fun getDiaryEntriesByTimeSpan(
        startDate: Date,
        endDate: Date
    ): LiveData<List<DiaryEntryInterface>> {
        val diaryEntries = MutableLiveData<List<DiaryEntryInterface>>()
        viewModelScope.launch(Dispatchers.IO) {
            diaryEntries.postValue(service.getDiaryEntriesByTimeSpan(startDate, endDate))
        }
        return diaryEntries
    }

    fun deleteDiaryEntryAndAssociatedObjects(diaryEntry: DiaryEntryInterface): LiveData<Boolean> {
        val operationComplete = MutableLiveData(false)
        viewModelScope.launch(Dispatchers.IO) {
            service.deleteDiaryEntryAndAssociatedObjects(diaryEntry)
            operationComplete.postValue(true)
        }
        return operationComplete
    }

    fun getIDOfLatestDiaryEntry(): LiveData<Long> {
        val idValue: MutableLiveData<Long> = MutableLiveData()
        viewModelScope.launch(Dispatchers.IO) {
            idValue.postValue(service.idOfLatestDiaryEntry)
        }
        return idValue
    }

    fun getDrugIntakesForDiaryEntry(id: Long): LiveData<Set<DrugIntakeInterface>> {
        val drugIntakes = MutableLiveData<Set<DrugIntakeInterface>>()
        viewModelScope.launch(Dispatchers.IO) {
            drugIntakes.postValue(service.getDrugIntakesForDiaryEntry(id))
        }
        return drugIntakes
    }

    fun getAllDrugs(): LiveData<List<DrugInterface>> {
        val drugs = MutableLiveData<List<DrugInterface>>()
        viewModelScope.launch(Dispatchers.IO) {
            drugs.postValue(service.allDrugs)
        }
        return drugs
    }

    fun getUserByID(userID: Long): LiveData<UserInterface> {
        val user = MutableLiveData<UserInterface>()
        viewModelScope.launch(Dispatchers.IO) {
            user.postValue(service.getUserByID(userID))
        }
        return user
    }

    fun storeUser(user: UserInterface): LiveData<Long> {
        val userID = MutableLiveData<Long>()
        viewModelScope.launch(Dispatchers.IO) {
            userID.postValue(service.storeUser(user))
        }
        return userID
    }

    fun updateUser(user: UserInterface) {
        viewModelScope.launch(Dispatchers.IO) {
            service.updateUser(user)
        }
    }

    fun initializeDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            service.initializeDatabase()
        }
    }
}