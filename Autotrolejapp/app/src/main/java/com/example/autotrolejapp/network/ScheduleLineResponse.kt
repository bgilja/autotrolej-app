package com.example.autotrolejapp.network

// {"Id":1,"PolazakId":"1300414","StanicaId":2364,"LinVarId":"1-A-0","Polazak":"05:10:00.0000000","RedniBrojStanice":1,
// "BrojLinije":"1","Smjer":"A","Varijanta":"0","NazivVarijanteLinije":"Pe\u0107ine Plumbum - Bivio","PodrucjePrometa":"Lokalni",
// "GpsX":14.473235,"GpsY":45.314145,"Naziv":"Pe\u0107ine Plumbum- O"}

import com.squareup.moshi.Json

data class ScheduleLineResponse(
    @Json(name = "Id") val id: Int,
    @Json(name = "PolazakId") val startId: Int,
    @Json(name = "StanicaId") val stationId: Int,
    @Json(name = "LinVarId") val lineVariantId: String,
    @Json(name = "Polazak") val startTimeStr: String,
    @Json(name = "RedniBrojStanice") val stationOrder: Int,
    @Json(name = "BrojLinije") val lineNumber: String,
    @Json(name = "Naziv") val lineName: String,
    @Json(name = "Smjer") val lineDirection: String,
    @Json(name = "Varijanta") val variant: String,
    @Json(name = "NazivVarijanteLinije") val lineVariantName: String,
    @Json(name = "PodrucjePrometa") val lineArea: String,
    @Json(name = "GpsX") val longitude: Double,
    @Json(name = "GpsY") val latitude: Double
) {
}