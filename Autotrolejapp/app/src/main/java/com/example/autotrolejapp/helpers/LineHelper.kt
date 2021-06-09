package com.example.autotrolejapp.helpers

import android.util.Log
import com.example.autotrolejapp.entities.Line
import java.util.function.Predicate

fun filterLinesByArea(lines: List<Line>, area: String): List<Line> {
    val result = mutableListOf<Line>()

    for (line in lines) {
        if (line.area != area) continue

        result.add(line)
    }

    return result
}

fun getDistinctLinesByLineNumber(lines: List<Line>) : List<Line> {
    val changeOrderOfThisLines = mutableListOf<Line>()
    val usedLines = mutableSetOf<String>()
    val result = mutableListOf<Line>()

    for (line in lines) {
        if (line.lineNumber in usedLines) continue
        /*if (line.lineNumber == "13" || line.lineNumber == "5B" || line.lineNumber == "KBC"){
            usedLines.add(line.lineNumber)
            changeOrderOfThisLines.add(line)
            continue
        }*/

        usedLines.add(line.lineNumber)
        result.add(line)
    }

    //TODO: hardcoded 3 cases when lines are not sorted well - fix later
    //2 cases are when line area = Local, 1 when lines area = wide
    /*changeOrderOfThisLines.forEach {
        if(it.lineNumber == "KBC") result.add(0, it)
        if(it.lineNumber == "13") result.add(it)
        if(it.lineNumber == "5B") result.add(0, it)
    }*/

    return result
}
