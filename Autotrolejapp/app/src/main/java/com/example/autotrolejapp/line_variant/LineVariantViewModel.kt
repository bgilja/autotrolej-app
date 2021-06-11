package com.example.autotrolejapp.line_variant

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.autotrolejapp.database.LineDatabaseDao
import com.example.autotrolejapp.database.ScheduleLineDatabaseDao
import com.example.autotrolejapp.database.StationDatabaseDao
import com.example.autotrolejapp.entities.BusLocation
import com.example.autotrolejapp.entities.Line
import com.example.autotrolejapp.entities.Station
import com.example.autotrolejapp.network.AutotrolejApi
import com.example.autotrolejapp.network.formatBusLocationResponse
import kotlinx.coroutines.*

class LineVariantViewModel(
    val stationDatabaseDao: StationDatabaseDao,
    val lineDatabaseDao: LineDatabaseDao,
    val scheduleLineDatabaseDao: ScheduleLineDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    val stations = stationDatabaseDao.getAll()
    val liveStations: MutableLiveData<List<Station>> = MutableLiveData(listOf())
    val busLines: MutableLiveData<List<Line>> = MutableLiveData(listOf())
    val allBusesOnWantedLineLive: MutableLiveData<List<BusLocation>> = MutableLiveData(listOf())
    val allBusesOnWantedLine: MutableList<BusLocation> = mutableListOf()
    lateinit var currentLineVariant : String

    var busLocations : List<BusLocation> = listOf()

    private var _selectedBusLocations = MutableLiveData<MutableList<BusLocation>>()
    val selectedBusLocations : LiveData<MutableList<BusLocation>>
        get() {
            return _selectedBusLocations
        }

    init {
        // getBusForLine("4-B-0")
        // getLineForStart(1305106)
    }

    fun getBusforWantedLine(lineVariantId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val data = AutotrolejApi.retrofitService.getCurrentBusLocations()
            busLocations = formatBusLocationResponse(data)
            busLocations.forEach{ busLocation ->
                busLines.postValue(scheduleLineDatabaseDao.getLineByStart(busLocation.startId))
                busLines.value?.forEach{ busLine ->
                    if(busLine.variantId ==  lineVariantId) {
                        allBusesOnWantedLine.add(busLocation)
                    }
                }
            }
            allBusesOnWantedLineLive.postValue(allBusesOnWantedLine)

            //change lat and long for buses that drive on selected lineVariant
            pingBusForLocation(allBusesOnWantedLine)
        }
    }

    fun getBusForLine(lineVariantId: String) {
        viewModelScope.launch {
            val data = AutotrolejApi.retrofitService.getCurrentBusLocations()
            val busLocationsCurrent = formatBusLocationResponse(data)

            val newSelectedBusLocations = mutableListOf<BusLocation>()

            busLocationsCurrent.forEach { busLocation ->
                val startId = busLocation.startId
                val busLines = scheduleLineDatabaseDao.getLineByStart(startId)

                val filteredLines = busLines.filter { line ->  line.variantId == lineVariantId }
                if (filteredLines.isNotEmpty()) {
                    newSelectedBusLocations.add(busLocation)
                    Log.d("BUS661", busLocation.toString())
                    getLineForStart(busLocation.startId)
                }
            }

            if (newSelectedBusLocations.size > 0) {
                _selectedBusLocations.value = newSelectedBusLocations
            }
        }
    }

    fun getLineForStart(startId: Int) {
        viewModelScope.launch {
            val lineX = scheduleLineDatabaseDao.getLineByStart(startId)
            Log.d("BUS661", lineX.toString())
        }
    }

    private fun pingBusForLocation(oldBusLocation: MutableList<BusLocation>): Job {
        return viewModelScope.launch {
            while(isActive) {
                try {
                    val data = AutotrolejApi.retrofitService.getCurrentBusLocations()
                    busLocations = formatBusLocationResponse(data)

                    busLocations.forEach { newBusLocation ->
                        oldBusLocation.find{ it.busName == newBusLocation.busName}?.latitude = newBusLocation.latitude
                        oldBusLocation.find{ it.busName == newBusLocation.busName}?.longitude = newBusLocation.longitude
                    }
                    allBusesOnWantedLineLive.postValue(oldBusLocation)
                    Log.d("Iz pingBusForLocation", oldBusLocation.toString())
                    Log.d(className, "DONE")
                } catch (e: Exception) {
                    Log.e(className, "FAIL")
                }
                delay(10000);
            }
        }
    }

    fun getLiveStations(lineVariantId: String) {
        currentLineVariant = lineVariantId
        viewModelScope.launch(Dispatchers.IO) {
            liveStations.postValue(lineDatabaseDao.getStations(lineVariantId))
        }

    }

    companion object {
        const val className = "LineVariantModel"
    }


}