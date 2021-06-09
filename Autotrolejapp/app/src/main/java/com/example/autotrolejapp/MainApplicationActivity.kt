package com.example.autotrolejapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.autotrolejapp.database.AutotrolejDatabase
import com.example.autotrolejapp.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

class MainApplicationActivity() : AppCompatActivity() {

    private var fetchStatus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_application)

        val scope = CoroutineScope(newSingleThreadContext("fetch_data"))
        scope.launch {
            // fetchStatus = fetchData()
            changeActivity()
        }
    }

    private fun changeActivity() {
        Log.d("FINISHED", fetchStatus.toString())
        val intent = Intent(this@MainApplicationActivity, MainActivity::class.java)
        startActivity(intent)
    }

    private suspend fun fetchData(): Boolean {
        coroutineScope {
            launch {
                getAutotrolejLines()
            }

            launch {
                getAutotrolejStations()
            }

            launch {
                getScheduleToday()
            }
        }
        return true
    }


    private suspend fun getAutotrolejLines(): Boolean {
        Log.d("FETCH DATA", "Request for autotrolej lines")
        try {
            val lineResponse = AutotrolejApi.retrofitService.getLines()
            val (lines, lineStations) = formatLineResponse(lineResponse)

            val lineDatabaseDao = AutotrolejDatabase.getInstance(application).lineDatabaseDao
            val lineStationDatabaseDao = AutotrolejDatabase.getInstance(application).lineStationDatabaseDao

            lineDatabaseDao.clear()
            lineDatabaseDao.insertMultiple(lines)

            lineStationDatabaseDao.clear()
            lineStationDatabaseDao.insertMultiple(lineStations)

            Log.d("FETCH DATA", "Updated lines and line stations")
            return true
        } catch (e: Exception) {
            Log.e("FETCH DATA", "FAIL")
        }
        return false
    }

    private suspend fun getAutotrolejStations(): Boolean {
        Log.d("FETCH DATA", "Request for autotrolej stations")
        try {
            val stationResponse = AutotrolejApi.retrofitService.getStations()
            val stations = formatStationResponse(stationResponse)

            val stationDatabaseDao = AutotrolejDatabase.getInstance(application).stationDatabaseDao

            stationDatabaseDao.clear()
            stationDatabaseDao.insertMultiple(stations)

            Log.d("FETCH DATA", "Updated stations")
            return true
        } catch (e: Exception) {
            Log.e("FETCH DATA", "FAIL")
        }
        return false
    }

    private suspend fun getScheduleToday(): Boolean {
        Log.d("FETCH DATA", "Request for autotrolej shedule for today")
        try {
            val scheduleResponse = AutotrolejApi.retrofitService.getScheduleToday()
            val (scheduleLines, scheduleStations) = formatScheduleResponse(scheduleResponse)

            val scheduleLineResponse = AutotrolejDatabase.getInstance(application).scheduleLineDatabaseDao
            val scheduleStationDatabaseDao = AutotrolejDatabase.getInstance(application).scheduleStationDatabaseDao

            scheduleLineResponse.clear()
            scheduleLineResponse.insertMultiple(scheduleLines)

            scheduleStationDatabaseDao.clear()
            scheduleStationDatabaseDao.insertMultiple(scheduleStations)

            Log.d("FETCH DATA", "Updated todays schedule")
            return true
        } catch (e: Exception) {
            Log.e("FETCH DATA", "FAIL")
        }
        return false
    }
}