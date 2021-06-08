package com.example.autotrolejapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.autotrolejapp.network.ScheduleLineResponse
import java.sql.Time

@Entity(tableName = ScheduleStation.TABLE_NAME)
data class ScheduleStation(

    @ColumnInfo(name = "line_variant_id")
    val lineVariantId: String,

    @ColumnInfo(name = "station_id")
    val stationId: Int,

    @ColumnInfo(name = "start_id")
    val startId: Int,

    @ColumnInfo(name = "time_str")
    val timeStr: String

): BaseEntity() {

    constructor(scheduleLineResponse: ScheduleLineResponse) :
            this(
                scheduleLineResponse.lineVariantId,
                scheduleLineResponse.stationId,
                scheduleLineResponse.startId,
                scheduleLineResponse.startTimeStr
            )

    companion object {
        const val TABLE_NAME = "schedule_stations_table"
    }
}