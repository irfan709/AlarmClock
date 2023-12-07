package com.example.alarmclock

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.alarmclock.databinding.ActivityAlarmRingBinding
import com.example.alarmclock.model.AlarmModel
import com.example.alarmclock.services.AlarmService
import com.example.alarmclock.viewmodel.AlarmsListViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Random

class AlarmRingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmRingBinding
    private lateinit var alarmListViewModel: AlarmsListViewModel
    private lateinit var alarmModel: AlarmModel
    private var songResourceId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmRingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }

        alarmListViewModel = ViewModelProvider(this)[AlarmsListViewModel::class.java]

        val bundle = intent.getBundleExtra(getString(R.string.bundle_alarm_object))
        if (bundle != null) {
            alarmModel = bundle.getSerializable(getString(R.string.alarm_object)) as AlarmModel
            songResourceId = alarmModel.alarmTone
        } else {
            finish()
            return
        }

        binding.dismissbtn.setOnClickListener {
            dismissAlarm()
        }

        binding.snoozebtn.setOnClickListener {
            snoozeAlarm()
        }
    }

    private fun snoozeAlarm() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.MINUTE, 5)

        alarmModel.hour = calendar.get(Calendar.HOUR_OF_DAY)
        alarmModel.minute = calendar.get(Calendar.MINUTE)
        alarmModel.title = "Snooze ${alarmModel.title}"

        alarmModel.schedule(applicationContext)

        val intentService = Intent(applicationContext, AlarmService::class.java)
        applicationContext.stopService(intentService)
        finish()
    }

    private fun dismissAlarm() {
        lifecycleScope.launch {
            alarmModel.started = false
            alarmModel.cancelAlarm(baseContext)
            alarmListViewModel.update(alarmModel)

            val intentService = Intent(applicationContext, AlarmService::class.java)
            applicationContext.stopService(intentService)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(false)
            setTurnScreenOn(false)
        } else {
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }
    }
}