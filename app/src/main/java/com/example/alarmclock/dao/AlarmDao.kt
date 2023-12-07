package com.example.alarmclock.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.alarmclock.model.AlarmModel

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAlarm(alarmModel: AlarmModel)

    @Update
    suspend fun updateAlarm(alarmModel: AlarmModel)

    @Delete
    suspend fun deleteAlarm(alarmModel: AlarmModel)

    @Query("SELECT * FROM alarm_table ORDER BY alarmId DESC")
    fun getAllAlarms(): LiveData<List<AlarmModel>>

    @Query("DELETE FROM alarm_table WHERE alarmId = :alarmId")
    suspend fun deleteSelectedAlarm(alarmId: Int)

    @Query("SELECT * FROM alarm_table WHERE alarmId = :id")
    fun getAlarmById(id: Int): AlarmModel?

}