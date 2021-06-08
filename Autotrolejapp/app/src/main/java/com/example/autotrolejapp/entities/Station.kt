package com.example.autotrolejapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.autotrolejapp.network.StationResponse

@Entity(tableName = Station.TABLE_NAME)
data class Station(

    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "short_name")
    val shortName: String = "",

    @ColumnInfo(name = "longitude")
    var longitude: Double?,

    @ColumnInfo(name = "latitude")
    var latitude: Double?

): BaseEntity() {

    constructor(stationResponse: StationResponse) :
            this(stationResponse.id, stationResponse.name, stationResponse.shortName, stationResponse.longitude, stationResponse.latitude)

    companion object {
        const val TABLE_NAME = "stations_table"
    }

    fun isValid(): Boolean {
        if (this.latitude == null || this.longitude == null) return false
        return true
    }
}