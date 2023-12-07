package com.example.alarmclock.model

import java.io.Serializable

data class AudioFile(

    val id: Int = 0,
    var name: String,
    var songResource: Int

): Serializable
