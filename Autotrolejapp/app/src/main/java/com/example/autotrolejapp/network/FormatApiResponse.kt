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

fun formatScheduleResponse(items: List<ScheduleLineResponse>): Pair<List<ScheduleLine>, List<ScheduleStation>> {
    val scheduleLines = mutableListOf<ScheduleLine>()
    val scheduleStations = mutableListOf<ScheduleStation>()

    var usedStarts = mutableSetOf<Int>()

    for (scheduleLineResponse in items) {
        val scheduleStation = ScheduleStation(scheduleLineResponse)
        scheduleStations.add(scheduleStation)

        if (usedStarts.contains(scheduleLineResponse.startId)) {
            continue
        }

        val scheduleLine = ScheduleLine(scheduleLineResponse)
        scheduleLines.add(scheduleLine)
        usedStarts.add(scheduleLine.startId)
    }

    return Pair(scheduleLines, scheduleStations)
}