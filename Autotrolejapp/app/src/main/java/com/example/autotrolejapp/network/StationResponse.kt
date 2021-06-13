package com.example.autotrolejapp.network

// {"StanicaId":1734,"Naziv":"1. maja","Kratki":"1. maja","GpsX":14.43316,"GpsY":45.333713}

import com.squareup.moshi.Json

data class StationResponse(
    @Json(name = "StanicaId") val id: Int,
    @Json(name = "Naziv") val name: String,
    @Json(name = "Kratki") val shortName: String,
    @Json(name = "GpsX") val _longitude: Double?,
    @Json(name = "GpsY") val _latitude: Double?
) {

    val longitude: Double
        get() {
            if (!isValid()) return 0.0
            return minOf(_longitude!!, _latitude!!)
        }

    val latitude: Double
        get() {
            if (!isValid()) return 0.0
            return maxOf(_longitude!!, _latitude!!)
        }

    fun isValid(): Boolean {
        return _latitude != null && _longitude != null
    }
}