package com.example.autotrolejapp.entities

import com.example.autotrolejapp.network.LineResponse

data class Line(
    var id: Int,
    val variantId: String,
    val variantName: String,
    val lineNumber: String,
    val direction: String,
    val variant: String,
    val area: String,
    private val lineStations: MutableList<LineStation> = mutableListOf()
) {
    constructor(lineResponse: LineResponse, lineStation: LineStation) :
            this(lineResponse.id, lineResponse.lineVariantId, lineResponse.name,
                    lineResponse.lineNumber, lineResponse.direction, lineResponse.variant, findArea(lineResponse.lineNumber)) {
        this.addLineStation(lineStation)
    }

    fun getLineStations() : List<LineStation> {
        return this.lineStations
    }

    fun addLineStation(lineStation: LineStation) {
        this.lineStations.add(lineStation)
    }

    companion object {

        val LOCAL: Set<String> = listOf(
            "KBC", "1", "1A", "1B", "2",
            "2A", "3", "3A", "4", "4A",
            "5", "5A", "6", "7", "7A", "8", "13").toSet()

        val NIGHT: Set<String> = listOf("102", "103").toSet()

        fun findArea(lineNumber: String): String {
            if (LOCAL.contains(lineNumber)) return "Local"
            if (NIGHT.contains(lineNumber)) return "Night"
            return "Wide"
        }
    }
}