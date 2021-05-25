package com.example.autotrolejapp.network

// {"Id":1,"LinVarId":"1-B-0","BrojLinije":"1","NazivVarijanteLinije":"Bivio-Pe\u0107ine","Smjer":"B","StanicaId":1795,"RedniBrojStanice":1,"Varijanta":"0"}

import com.squareup.moshi.Json

data class LineResponse(
    @Json(name = "Id") val id: Int,
    @Json(name = "LinVarId") val lineVariantId: String,
    @Json(name = "BrojLinije") val lineNumber: String,
    @Json(name = "NazivVarijanteLinije") val name: String,
    @Json(name = "Smjer") val direction: String,
    @Json(name = "StanicaId") val stationId: Int,
    @Json(name = "RedniBrojStanice") val stationOrder: Int,
    @Json(name = "Varijanta") val variant: String
) {
}