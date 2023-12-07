package com.example.alarmclock.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.alarmclock.model.AlarmModel
import com.example.alarmclock.repository.AlarmRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlarmViewModel(application: Application) : AndroidViewModel(application) {
    private val alarmRepository: AlarmRepository = AlarmRepository(application)

    suspend fun insertOrUpdate(alarm: AlarmModel) {
        withContext(Dispatchers.IO) {
            alarmRepository.insertOrUpdate(alarm)
        }
    }
}