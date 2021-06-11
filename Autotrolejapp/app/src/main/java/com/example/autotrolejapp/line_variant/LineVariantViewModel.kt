package com.example.autotrolejapp.line_variant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.autotrolejapp.database.LineDatabaseDao
import com.example.autotrolejapp.database.ScheduleLineDatabaseDao
import com.example.autotrolejapp.entities.BusLocation
import com.example.autotrolejapp.entities.Station
import com.example.autotrolejapp.network.AutotrolejApi
import com.example.autotrolejapp.network.formatBusLocationResponse
import kotlinx.coroutines.*

class LineVariantViewModel(
    private val lineDatabaseDao: LineDatabaseDao,
    private val scheduleLineDatabaseDao: ScheduleLineDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val _selectedBusLocations = MutableLiveData<MutableList<BusLocation>>()
    val selectedBusLocations : LiveData<MutableList<BusLocation>>
        get() {
            return _selectedBusLocations
        }

    private var _lineStations = MutableLiveData<List<Station>>()
    val lineStations: LiveData<List<Station>>
        get() {
            return _lineStations
        }

    fun getBusForLine(lineVariantIds: List<String>) {
        if (lineVariantIds.isEmpty()) return

        viewModelScope.launch {
            val data = AutotrolejApi.retrofitService.getCurrentBusLocations()
            val busLocationsCurrent = formatBusLocationResponse(data)

            if (busLocationsCurrent.isNotEmpty()) {
                val newSelectedBusLocations = mutableListOf<BusLocation>()

                busLocationsCurrent.forEach { busLocation ->
                    val startId = busLocation.startId
                    val busLines = scheduleLineDatabaseDao.getLineByStart(startId)

                    val filteredLines = busLines.filter { line ->
                        lineVariantIds.any { lineVariantId -> line.variantId == lineVariantId }
                    }

                    if (filteredLines.isNotEmpty()) {
                        newSelectedBusLocations.add(busLocation)
                    }
                }

                if (newSelectedBusLocations.size > 0) {
                    _selectedBusLocations.value = newSelectedBusLocations
                }
            }
        }
    }

    fun getLineStations(lineVariantIds: List<String>) {
        if (lineVariantIds.isEmpty()) return

        viewModelScope.launch {
            val stations = mutableListOf<Station>()

            lineVariantIds.forEach { lineVariantId ->
                val x = lineDatabaseDao.getStations(lineVariantId)
                x.forEach { station -> stations.add(station) }
            }

            _lineStations.value = stations
        }
    }

    companion object {
        const val className = "LineVariantModel"
    }


}