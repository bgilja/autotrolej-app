package com.example.autotrolejapp.home

import android.app.Application
import androidx.lifecycle.*
import com.example.autotrolejapp.database.LineDatabaseDao
import com.example.autotrolejapp.database.LineStationDatabaseDao
import com.example.autotrolejapp.database.StationDatabaseDao

class HomeViewModel(
    val lineDatabaseDao: LineDatabaseDao,
    val stationDatabaseDao: StationDatabaseDao,
    val lineStationDatabaseDao: LineStationDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    val _lines = lineDatabaseDao.getAll()
    val _stations = stationDatabaseDao.getAll()
    val _lineStations = lineStationDatabaseDao.getAll()
}