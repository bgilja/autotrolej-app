package com.example.autotrolejapp.network

import android.util.Log
import com.example.autotrolejapp.entities.*
import com.example.autotrolejapp.entities.Schedule

fun formatLineResponse(items: List<LineResponse>) : List<Line> {
    var lines = mutableListOf<Line>()

    var usedLines = mutableSetOf<String>()

    Log.d(object{}.javaClass.enclosingMethod!!.name, "")
    for (lineResponse in items) {
        val lineStation = LineStation(lineResponse)

        if (usedLines.contains(lineResponse.lineVariantId)) {
            lines.last().addLineStation(lineStation)
            continue
        }

        lines.add(Line(lineResponse, lineStation))
        usedLines.add(lineResponse.lineVariantId)
    }
    return lines
}

fun formatStationResponse(items: List<StationResponse>) : List<Station> {
    var stations = mutableListOf<Station>()

    for (stationResponse in items)
        stations.add(Station(stationResponse))

    return stations
}

fun formatBusLocationResponse(items: List<BusLocationResponse>) : List<BusLocation> {
    val busLocations = mutableListOf<BusLocation>()

    for (busLocationResponse in items)
        busLocations.add(BusLocation(busLocationResponse))

    return busLocations
}

fun formatScheduleResponse(items: List<ScheduleLineResponse>): Schedule {
    val schedule = Schedule()

    var usedStarts = mutableSetOf<Int>()
    for (scheduleLineResponse in items) {
        val scheduleStation = ScheduleStation(scheduleLineResponse)

        if (usedStarts.contains(scheduleLineResponse.startId)) {
            schedule.getScheduleLines().last().addScheduleStation(scheduleStation)
            continue
        }

        val scheduleLine = ScheduleLine(scheduleLineResponse, scheduleStation)
        schedule.addScheduleLine(scheduleLine)
        usedStarts.add(scheduleLine.startId)
    }
    return schedule
}