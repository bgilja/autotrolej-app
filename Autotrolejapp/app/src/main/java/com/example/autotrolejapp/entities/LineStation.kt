package com.example.autotrolejapp.entities

import com.example.autotrolejapp.network.LineResponse

data class LineStation(
    val lineVariantId: String,
    val stationId: Int,
    val order: Int
) {
    constructor(lineResponse: LineResponse) :
            this(lineResponse.lineVariantId, lineResponse.stationId, lineResponse.stationOrder) {}
}