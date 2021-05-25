package com.example.autotrolejapp.entities

import com.example.autotrolejapp.network.ScheduleLineResponse
import java.sql.Time

data class Schedule(
    private val scheduleLines: MutableList<ScheduleLine> = mutableListOf()
) {

    fun getScheduleLines() : List<ScheduleLine> {
        return this.scheduleLines
    }

    fun addScheduleLine(scheduleLine: ScheduleLine) {
        this.scheduleLines.add(scheduleLine)
    }
}

data class ScheduleLine(
    val startId: Int,
    val lineVariantId: String,
    val trafficArea: String,
    val scheduleStations : MutableList<ScheduleStation> = mutableListOf()
) {
    constructor(scheduleLineResponse: ScheduleLineResponse, scheduleStation: ScheduleStation) :
            this(scheduleLineResponse.startId, scheduleLineResponse.lineVariantId, scheduleLineResponse.lineArea) {
        this.addScheduleStation(scheduleStation)
    }

    fun addScheduleStation(scheduleStation: ScheduleStation) {
        this.scheduleStations.add(scheduleStation)
    }
}

data class ScheduleStation(
    val stationId: Int,
    val startId: Int,
    val time: Time?,
    val order: Int,
    val direction: String
) {
    constructor(scheduleLineResponse: ScheduleLineResponse) :
            this(scheduleLineResponse.stationId, scheduleLineResponse.startId, null, scheduleLineResponse.stationOrder, scheduleLineResponse.lineDirection) {}
}