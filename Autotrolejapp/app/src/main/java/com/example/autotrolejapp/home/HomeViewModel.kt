package com.example.autotrolejapp.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.autotrolejapp.database.LineDatabaseDao
import com.example.autotrolejapp.database.LineStationDatabaseDao
import com.example.autotrolejapp.database.StationDatabaseDao
import com.example.autotrolejapp.network.AutotrolejApi
import com.example.autotrolejapp.network.formatLineResponse
import com.example.autotrolejapp.network.formatStationResponse
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch

class HomeViewModel(
    val lineDatabaseDao: LineDatabaseDao,
    val stationDatabaseDao: StationDatabaseDao,
    val lineStationDatabaseDao: LineStationDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    val _lines = lineDatabaseDao.getAll()
    val lines
        get() = _lines.value.orEmpty()

    val _stations = stationDatabaseDao.getAll()

    val _lineStations = lineStationDatabaseDao.getAll()

    init {

        if (_lines.value?.isEmpty() == true  || _lineStations.value?.isEmpty() == true)  getAutotrolejLines()
        if (_stations.value?.isEmpty() == true)  getAutotrolejStations()
    }

    fun printStations() {
        Log.d("230438902482903", lines.size.toString())
        if (lines.isEmpty()) return

        val variantId = lines.first().variantId
        getLineStations(variantId)
    }

    private fun getLineStations(variantId: String) {
        Log.d("230438902482903", variantId)
        viewModelScope.launch {
            lineStationDatabaseDao.getByLine(variantId).let {
                val lineStations = it.value.orEmpty()
                val stations = lineStations.map { x -> x.stationId }

                for (station in stations) {
                    Log.d("STATION", station.toString())
                }

                val result = stationDatabaseDao.getMultipleById(stations).value.orEmpty()
            }
        }
    }

    private fun getAutotrolejLines() {
        viewModelScope.launch {
            Log.d(className, "Request for autotrolej lines")
            try {
                val data = AutotrolejApi.retrofitService.getLines()
                val (x, y) = formatLineResponse(data)

                lineDatabaseDao.clear()
                lineDatabaseDao.insertMultiple(x)

                lineStationDatabaseDao.clear()
                lineStationDatabaseDao.insertMultiple(y)
            } catch (e: Exception) {
                Log.e(className, "FAIL")
            }
        }
    }

    private fun getAutotrolejStations() {
        viewModelScope.launch {
            Log.d(className, "Request for autotrolej stations")
            try {
                val data = AutotrolejApi.retrofitService.getStations()
                val x = formatStationResponse(data)

                stationDatabaseDao.clear()
                stationDatabaseDao.insertMultiple(x)
            } catch (e: Exception) {
                Log.e(className, "FAIL")
            }
        }
    }

    companion object {
        const val className = "HomeViewModel"
    }
}