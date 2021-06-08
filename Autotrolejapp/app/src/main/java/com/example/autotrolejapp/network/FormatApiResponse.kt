package com.example.autotrolejapp.network

import com.example.autotrolejapp.entities.*

fun formatLineResponse(items: List<LineResponse>) : Pair<List<Line>, List<LineStation>> {
    var lines = mutableListOf<Line>()
    var lineStations = mutableListOf<LineStation>()

    var usedLines = mutableSetOf<String>()

    for (lineResponse in items) {
        val lineStation = LineStation(lineResponse)
        lineStations.add(lineStation)

        if (usedLines.contains(lineResponse.lineVariantId)) {
            continue
        }

        lines.add(Line(lineResponse))
        usedLines.add(lineResponse.lineVariantId)
    }

    return Pair(lines, lineStations)
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