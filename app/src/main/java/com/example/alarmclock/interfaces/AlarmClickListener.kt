package com.example.alarmclock.interfaces

import com.example.alarmclock.model.AlarmModel

interface AlarmClickListener {
    fun onAlarmClick(alarm: AlarmModel)
    fun onAlarmDelete(alarm: AlarmModel)
    fun onToggle(alarm: AlarmModel)
}