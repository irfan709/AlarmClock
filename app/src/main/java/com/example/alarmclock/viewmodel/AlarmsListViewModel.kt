package com.example.alarmclock.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.alarmclock.model.AlarmModel
import com.example.alarmclock.repository.AlarmRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlarmsListViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmRepository: AlarmRepository = AlarmRepository(application)
    val alarmsLiveData: LiveData<List<AlarmModel>> = alarmRepository.getAlarmsLiveData()

    suspend fun update(alarm: AlarmModel) {
        withContext(Dispatchers.IO) {
            alarmRepository.update(alarm)
        }
    }

    fun getAllAlarms(): LiveData<List<AlarmModel>> {
        return alarmsLiveData
    }

    suspend fun delete(alarmId: Int) {
        withContext(Dispatchers.IO) {
            alarmRepository.delete(alarmId)
        }
    }
}