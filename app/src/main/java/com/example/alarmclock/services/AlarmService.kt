package com.example.alarmclock.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.alarmclock.AlarmRingActivity
import com.example.alarmclock.R
import com.example.alarmclock.model.AlarmModel
import java.io.IOException

class AlarmService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator
    private lateinit var alarmModel: AlarmModel
    private lateinit var alarmTone: Uri

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer().apply {
            isLooping = true
        }
        vibrator = getSystemService(Vibrator::class.java) as Vibrator
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ObsoleteSdkInt")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bundle = intent?.getBundleExtra(getString(R.string.bundle_alarm_object))

        if (bundle != null) {
            alarmModel = (bundle.getSerializable(getString(R.string.alarm_object)) as? AlarmModel)!!

            val intent = Intent(this, AlarmRingActivity::class.java)
            intent.putExtra(getString(R.string.bundle_alarm_object), bundle)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            var alarmTitle: String = getString(R.string.alarm_title)

            if (alarmModel.alarmTone == 0) {
                alarmTone = Uri.parse("android.resource://${packageName}/${R.raw.leo}")
                try {
                    mediaPlayer.setDataSource(this, alarmTone)
                    mediaPlayer.prepareAsync()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                alarmTone = Uri.parse("android.resource://${packageName}/${alarmModel.alarmTone}")
                try {
                    mediaPlayer.setDataSource(this, alarmTone)
                    mediaPlayer.prepareAsync()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            val CHANNEL_ID = "alarm"
            val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(CHANNEL_ID)
                NotificationCompat.Builder(this, CHANNEL_ID)
            } else {
                @Suppress("DEPRECATION")
                NotificationCompat.Builder(this)
            }.setContentTitle("Alarm Ringing...")
                .setContentText(alarmTitle)
                .setSmallIcon(R.drawable.baseline_alarm_24)
                .setSound(null)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .build()

            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
            }

            if (alarmModel.vibrate) {
                val pattern = longArrayOf(0, 100, 1000)
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
            }

            startForeground(1, notification)
        }
        return START_STICKY
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for alarm notifications"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
        vibrator.cancel()
    }
}