package com.example.autotrolejapp.home

import android.app.Application
import androidx.lifecycle.*
import com.example.autotrolejapp.database.LineDatabaseDao
import com.example.autotrolejapp.database.LineStationDatabaseDao
import com.example.autotrolejapp.database.StationDatabaseDao
import com.example.autotrolejapp.entities.Line
import kotlinx.coroutines.launch

class HomeViewModel(
    val lineDatabaseDao: LineDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var _lines = lineDatabaseDao.getAll()
    val lines: LiveData<List<Line>>
        get() {
            return _lines
        }
}