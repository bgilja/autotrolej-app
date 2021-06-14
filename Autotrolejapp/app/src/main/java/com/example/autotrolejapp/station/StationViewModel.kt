package com.example.autotrolejapp.station

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.autotrolejapp.database.ScheduleStationDatabaseDao
import com.example.autotrolejapp.database.StationDatabaseDao
import com.example.autotrolejapp.entities.ScheduleStation
import com.example.autotrolejapp.entities.Station
import kotlinx.coroutines.launch

class StationViewModel(
    private val stationDatabaseDao: StationDatabaseDao,
    private val scheduleStationDatabaseDao: ScheduleStationDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var _station = MutableLiveData<Station>()
    val station : LiveData<Station>
        get() {
            return _station
        }

    private var _scheduleStations = MutableLiveData<List<ScheduleStation>>()
    val scheduleStations: LiveData<List<ScheduleStation>>
        get() {
            return _scheduleStations
        }

    fun getStation(stationIdentity: Long) {
        viewModelScope.launch {
            _station.value = stationDatabaseDao.get(stationIdentity)
        }
    }

    fun getScheduleStations(stationId: Int) {
        viewModelScope.launch {
            _scheduleStations.value = scheduleStationDatabaseDao.getByStationId(stationId)
            Log.d("SCHEUDLE STATIONS", _scheduleStations.value?.size.toString())
        }
    }

}