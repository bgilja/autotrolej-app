package com.example.autotrolejapp.map

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.autotrolejapp.database.StationDatabaseDao
import com.example.autotrolejapp.entities.BusLocation
import com.example.autotrolejapp.entities.Station
import com.example.autotrolejapp.network.AutotrolejApi
import com.example.autotrolejapp.network.formatBusLocationResponse
import kotlinx.coroutines.launch


class MapViewModel(
    stationDatabaseDao: StationDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val _stations = stationDatabaseDao.getAll()
    val stations: LiveData<List<Station>>
        get() = _stations

    private val _busLocations = MutableLiveData<List<BusLocation>>()
    val busLocations: LiveData<List<BusLocation>>
        get() = _busLocations

    init {
        getAutotrolejBusLocations()
    }

    fun getAutotrolejBusLocations() {
        viewModelScope.launch {
            Log.d(className, "Request for autotrolej bus location")
            try {
                val data = AutotrolejApi.retrofitService.getCurrentBusLocations()
                _busLocations.value = formatBusLocationResponse(data)
                Log.d(className, "DONE")
            } catch (e: Exception) {
                Log.e(className, "FAIL")
            }
        }
    }

    companion object {
        const val className = "MapViewModel"
    }


}