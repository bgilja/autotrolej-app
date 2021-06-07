package com.example.autotrolejapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.autotrolejapp.network.LineResponse

@Entity(tableName = LineStation.TABLE_NAME)
data class LineStation(

    @ColumnInfo(name = "line_variant_id")
    val lineVariantId: String = "",

    @ColumnInfo(name = "station_id")
    val stationId: Int = 0,

    @ColumnInfo(name = "station_order")
    var order: Int = 0

): BaseEntity() {

    constructor(lineResponse: LineResponse) :
            this(lineResponse.lineVariantId, lineResponse.stationId, lineResponse.stationOrder)

    companion object {

        const val TABLE_NAME: String = "line_station_table"

    }
}