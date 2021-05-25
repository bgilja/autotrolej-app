package com.example.autotrolejapp.entities

import com.example.autotrolejapp.network.StationResponse

data class Station(
    val id: Int,
    val name: String,
    val shortName: String,
    val location: Location
) {
    constructor(stationResponse: StationResponse) :
            this(stationResponse.id, stationResponse.name, stationResponse.shortName, Location(stationResponse.longitude, stationResponse.latitude))
}