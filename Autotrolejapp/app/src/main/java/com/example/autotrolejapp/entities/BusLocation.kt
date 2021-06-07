package com.example.autotrolejapp.entities

import com.example.autotrolejapp.network.BusLocationResponse
import java.sql.Time

data class BusLocation(
    val busName: String,
    val startId: Int,
    val stationId: Int,
    val time: Time?,
    val longitude: Double?,
    val latitude: Double?,
) {
    constructor(busLocationResponse: BusLocationResponse) :
            this(
                busLocationResponse.busName,
                busLocationResponse.startId,
                busLocationResponse.stationId,
                null,
                busLocationResponse.longitude,
                busLocationResponse.latitude)
}