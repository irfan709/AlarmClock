package com.example.alarmclock.model

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.alarmclock.R
import com.example.alarmclock.broadcastreceiver.AlarmReceiver
import java.io.Serializable
import java.util.Calendar

@Entity(tableName = "alarm_table")
data class AlarmModel(

    @PrimaryKey(autoGenerate = true)
    val alarmId: Int,

    var title: String,

    var hour: Int,

    var minute: Int,

    var alarmTone: Int,

    var started: Boolean,

    var recurring: Boolean,

    var vibrate: Boolean,

    var monday: Boolean,

    var tuesday: Boolean,

    var wednesday: Boolean,

    var thursday: Boolean,

    var friday: Boolean,

    var saturday: Boolean,

    var sunday: Boolean

): Serializable {

    @SuppressLint("ScheduleExactAlarm")
    fun schedule(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        val bundle = Bundle().apply {
            putSerializable(context.getString(R.string.alarm_object), this@AlarmModel)
        }
        intent.putExtra(context.getString(R.string.bundle_alarm_object), bundle)
        val alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                set(Calendar.DAY_OF_MONTH, get(Calendar.DAY_OF_MONTH) + 1)
            }
        }

        if (!recurring) {
            Toast.makeText(context, "One time alarm set...", Toast.LENGTH_LONG).show()
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmPendingIntent
            )
        } else {
            val DAILY_ALARM = 24 * 60 * 60 * 1000L
            Toast.makeText(context, "Recurring alarm set...", Toast.LENGTH_LONG).show()
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                DAILY_ALARM,
                alarmPendingIntent
            )
        }

        started = true
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(alarmPendingIntent)
        started = false
    }

    fun getRecurringDaysText(): String? {
        if (!recurring) {
            return null
        }

        val days = StringBuilder()
        if (monday) days.append("Mo ")
        if (tuesday) days.append("Tu ")
        if (wednesday) days.append("We ")
        if (thursday) days.append("Th ")
        if (friday) days.append("Fr ")
        if (saturday) days.append("Sa ")
        if (sunday) days.append("Su ")

        return days.toString()
    }
}