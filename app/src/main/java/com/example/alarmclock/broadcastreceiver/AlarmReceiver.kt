package com.example.alarmclock.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import com.example.alarmclock.R
import com.example.alarmclock.model.AlarmModel
import com.example.alarmclock.services.AlarmService
import com.example.alarmclock.services.RescheduleAlarmService
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            rescheduleAlarm(context)
        } else {
            intent?.getBundleExtra(context?.getString(R.string.bundle_alarm_object))
                ?.let { bundle ->
                    val alarm =
                        bundle.getSerializable(context?.getString(R.string.alarm_object)) as? AlarmModel

                    val toast = String.format("Alarm Received")
                    Toast.makeText(context, toast, Toast.LENGTH_LONG).show()

                    alarm?.let {
                        if (!it.recurring || isTodayAlarm(it)) {
                            startAlarm(context, it)
                        }
                    }
                }
        }
    }

    private fun isTodayAlarm(alarm: AlarmModel): Boolean {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_WEEK)

        return when (today) {
            Calendar.MONDAY -> alarm.monday
            Calendar.TUESDAY -> alarm.tuesday
            Calendar.WEDNESDAY -> alarm.wednesday
            Calendar.THURSDAY -> alarm.thursday
            Calendar.FRIDAY -> alarm.friday
            Calendar.SATURDAY -> alarm.saturday
            Calendar.SUNDAY -> alarm.sunday
            else -> false
        }
    }

    private fun startAlarm(context: Context?, alarm: AlarmModel) {
        val intentService = Intent(context, AlarmService::class.java)
        val bundle = Bundle().apply {
            putSerializable(context?.getString(R.string.alarm_object), alarm)
        }

        intentService.putExtra(context?.getString((R.string.bundle_alarm_object)), bundle)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(intentService)
        } else {
            context?.startService(intentService)
        }
    }

    private fun rescheduleAlarm(context: Context?) {
        val intentService = Intent(context, RescheduleAlarmService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(intentService)
        } else {
            context?.startService(intentService)
        }
    }
}