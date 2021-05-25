package com.example.autotrolejapp.network

// {"StanicaId":1734,"Naziv":"1. maja","Kratki":"1. maja","GpsX":14.43316,"GpsY":45.333713}

import com.squareup.moshi.Json

data class StationResponse(
    @Json(name = "StanicaId") val id: Int,
    @Json(name = "Naziv") val name: String,
    @Json(name = "Kratki") val shortName: String,
    @Json(name = "GpsX") val longitude: Double?,
    @Json(name = "GpsY") val latitude: Double?
) {
}