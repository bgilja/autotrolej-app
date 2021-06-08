package com.example.autotrolejapp.home

import android.app.Application
import androidx.lifecycle.*
import com.example.autotrolejapp.database.LineDatabaseDao
import com.example.autotrolejapp.database.LineStationDatabaseDao
import com.example.autotrolejapp.database.StationDatabaseDao

class HomeViewModel(
    val lineDatabaseDao: LineDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    val _lines = lineDatabaseDao.getAll()
}