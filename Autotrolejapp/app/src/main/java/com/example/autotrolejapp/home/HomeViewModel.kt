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
import kotlinx.coroutines.launch

class HomeViewModel(
    val lineDatabaseDao: LineDatabaseDao,
    val stationDatabaseDao: StationDatabaseDao,
    val lineStationDatabaseDao: LineStationDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    val lines = lineDatabaseDao.getAll()
    val stations = stationDatabaseDao.getAll()
    val lineStations = lineStationDatabaseDao.getAll()

    init {

        if (lines.value?.isEmpty() == true  || lineStations.value?.isEmpty() == true)  getAutotrolejLines()
        if (stations.value?.isEmpty() == true)  getAutotrolejStations()
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