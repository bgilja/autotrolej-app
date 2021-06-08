package com.example.autotrolejapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.autotrolejapp.network.ScheduleLineResponse

@Entity(tableName = ScheduleLine.TABLE_NAME)
data class ScheduleLine(

    @ColumnInfo(name = "start_id")
    val startId: Int,

    @ColumnInfo(name = "line_variant_id")
    val lineVariantId: String,

    @ColumnInfo(name = "traffic_area")
    val trafficArea: String

): BaseEntity() {

    constructor(scheduleLineResponse: ScheduleLineResponse) :
            this(
                scheduleLineResponse.startId,
                scheduleLineResponse.lineVariantId,
                scheduleLineResponse.lineArea
            )

    companion object {
        const val TABLE_NAME = "schedule_lines_table"
    }
}