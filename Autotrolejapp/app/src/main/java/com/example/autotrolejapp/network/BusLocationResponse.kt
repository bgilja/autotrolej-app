package com.example.autotrolejapp.network

// {"Autobus":"118","PolazakId":1303755,"StanicaId":2293,"GpsX":45.226319,"GpsY":14.248551,"Vrijeme":"May 12 2021  1:04PM"}

import com.squareup.moshi.Json

data class BusLocationResponse(
    @Json(name = "Autobus") val busName: String,
    @Json(name = "PolazakId") val startId: Int,
    @Json(name = "StanicaId") val stationId: Int,
    @Json(name = "GpsX") val _longitude: Double,
    @Json(name = "GpsY") val _latitude: Double,
    @Json(name = "Vrijeme") val timeStr: String
) {

    val longitude: Double
        get() {
            return minOf(_longitude, _latitude)
        }

    val latitude: Double
        get() {
            return maxOf(_longitude, _latitude)
        }
}