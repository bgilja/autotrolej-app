package com.example.autotrolejapp.helpers

import com.example.autotrolejapp.entities.Line

fun filterLinesByArea(lines: List<Line>, area: String): MutableList<Line> {
    val result = mutableListOf<Line>()

    for (line in lines) {
        if (line.area != area) continue

        result.add(line)
    }

    return result
}