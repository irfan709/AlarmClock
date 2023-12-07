package com.example.alarmclock.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.alarmclock.dao.AlarmDao
import com.example.alarmclock.database.AlarmDatabase
import com.example.alarmclock.model.AlarmModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlarmRepository(application: Application) {
    private val alarmDao: AlarmDao
    private val alarmsLiveData: LiveData<List<AlarmModel>>

    init {
        val db = AlarmDatabase.getDatabase(application)
        alarmDao = db.alarmDao()
        alarmsLiveData = alarmDao.getAllAlarms()
    }

    suspend fun insert(alarm: AlarmModel) {
        withContext(Dispatchers.IO) {
            alarmDao.addAlarm(alarm)
        }
    }

    suspend fun update(alarm: AlarmModel) {
        withContext(Dispatchers.IO) {
            alarmDao.updateAlarm(alarm)
        }
    }

    fun getAlarmsLiveData(): LiveData<List<AlarmModel>> {
        return alarmsLiveData
    }

    suspend fun delete(alarmId: Int) {
        withContext(Dispatchers.IO) {
            alarmDao.deleteSelectedAlarm(alarmId)
        }
    }

    suspend fun insertOrUpdate(alarm: AlarmModel) {
        if (getAlarmById(alarm.alarmId) != null) {
            update(alarm)
        } else {
            insert(alarm)
        }
    }

    private fun getAlarmById(alarmId: Int): AlarmModel? {
        return alarmDao.getAlarmById(alarmId)
    }
}