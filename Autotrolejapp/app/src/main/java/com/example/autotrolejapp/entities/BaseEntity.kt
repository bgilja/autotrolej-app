package com.example.autotrolejapp.entities

import androidx.room.PrimaryKey
import java.sql.Time

open class BaseEntity(
    @PrimaryKey(autoGenerate = true)
    var identity: Long = 0L,
) {

    fun formatTimeStr(timeStr: String) {
        null
    }
}