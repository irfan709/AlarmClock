package com.example.alarmclock.services

import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.example.alarmclock.repository.AlarmRepository

class RescheduleAlarmService : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val alarmRepository = AlarmRepository(application)

        alarmRepository.getAlarmsLiveData().observe(this, Observer { alarms ->
            alarms?.forEach { alarm ->
                if (alarm.started) {
                    alarm.schedule(applicationContext)
                }
            }
        })

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}