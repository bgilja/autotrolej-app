package com.example.autotrolejapp.map

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.autotrolejapp.database.StationDatabaseDao
import com.example.autotrolejapp.entities.BusLocation
import com.example.autotrolejapp.entities.Station
import com.example.autotrolejapp.network.AutotrolejApi
import com.example.autotrolejapp.network.formatBusLocationResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MapViewModel(
    val stationDatabaseDao: StationDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val _stations = MutableLiveData(emptyList<Station>())
    val stations: LiveData<List<Station>>
        get() {
            return _stations
        }

    private val _busLocations = MutableLiveData<List<BusLocation>>()
    val busLocations: LiveData<List<BusLocation>>
        get() {
            return _busLocations
        }

    init {
        getAutotrolejBusLocations()

        viewModelScope.launch {
            val validStations = stationDatabaseDao.getAllSuspend().filter { x -> x.isValid() }

            val stationsSlow = mutableListOf<Station>()
            Log.d("STATIONS", validStations.size.toString())

            for (station in validStations) {
                if (stationsSlow.size > 0 && stationsSlow.size % 100 == 0) {
                    delay(500)
                    _stations.value = stationsSlow
                }

                stationsSlow.add(station)
            }
        }
    }

    public fun getAutotrolejBusLocations() {
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