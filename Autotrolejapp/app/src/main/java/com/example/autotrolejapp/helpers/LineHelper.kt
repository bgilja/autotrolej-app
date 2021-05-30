package com.example.autotrolejapp.helpers

import com.example.autotrolejapp.entities.Line

fun filterLinesByArea(lines: List<Line>, area: String): List<Line> {
    val result = mutableListOf<Line>()

    for (line in lines) {
        if (line.area != area) continue

        result.add(line)
    }

    return result
}

fun getDistinctLinesByLineNumber(lines: List<Line>) : List<Line> {
    val usedLines = mutableSetOf<String>()
    val result = mutableListOf<Line>()

    for (line in lines) {
        if (line.lineNumber in usedLines) continue

        usedLines.add(line.lineNumber)
        result.add(line)
    }

    return result
}
