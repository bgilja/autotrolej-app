package com.example.autotrolejapp.entities

import com.example.autotrolejapp.network.BusLocationResponse
import java.sql.Time

data class BusLocation(
    val busName: String,
    val startId: Int,
    val stationId: Int,
    val time: Time?,
    val location: Location
) {
    constructor(busLocationResponse: BusLocationResponse) :
            this(busLocationResponse.busName, busLocationResponse.startId, busLocationResponse.stationId,
                null, Location(busLocationResponse.longitude, busLocationResponse.latitude)
            )
}