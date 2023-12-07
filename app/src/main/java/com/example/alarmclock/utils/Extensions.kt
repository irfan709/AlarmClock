package com.example.alarmclock.utils

import android.os.Build
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import java.util.Calendar

fun View.getTimePickerHour(tp: TimePicker): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        tp.hour
    } else {
        tp.hour
    }
}

fun View.getTimePickerMinute(tp: TimePicker): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        tp.minute
    } else {
        tp.minute
    }
}

fun View.toDay(day: Int): String {
    return when (day) {
        Calendar.SUNDAY -> "Sunday"
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        Calendar.SATURDAY -> "Saturday"
        else -> throw Exception("Could not locate day")
    }
}

fun View.getDay(hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    return if (calendar.timeInMillis <= System.currentTimeMillis()) {
        "Tomorrow"
    } else {
        "Today"
    }
}