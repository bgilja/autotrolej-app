package com.example.autotrolejapp.helpers

import com.example.autotrolejapp.entities.Station
import com.example.autotrolejapp.network.StationResponse

fun filterStationsByNameAndGeoXGeoY(stations: List<Station>){
    //TODO: dodat nest tu ako bude trebalo
}

fun formatStationResponse(items: List<StationResponse>): List<Station> {
    val stations = mutableListOf<Station>()

    for (item in items) {
        val station = Station(item)
        stations.add(station)
    }

    return stations
}
