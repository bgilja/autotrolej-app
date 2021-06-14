package com.example.autotrolejapp.station

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.autotrolejapp.R
import com.example.autotrolejapp.database.AutotrolejDatabase
import com.example.autotrolejapp.line_variant.LineVariantViewModel
import com.example.autotrolejapp.line_variant.LineVariantViewModelFactory


private const val ARG_PARAM1 = "param1"

class StationFragment : Fragment() {
    private var stationIdentity: Long = -1


    private val viewModel: StationViewModel by lazy {
        val application = requireNotNull(this.activity).application
        val stationDatabaseDao = AutotrolejDatabase.getInstance(application).stationDatabaseDao
        val scheduleStationDatabaseDao = AutotrolejDatabase.getInstance(application).scheduleStationDatabaseDao

        val viewModelFactory = StationViewModelFactory(stationDatabaseDao, scheduleStationDatabaseDao, application)
        ViewModelProvider(this, viewModelFactory).get(StationViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            stationIdentity = it.getLong(ARG_PARAM1)
            Log.d("STATION", stationIdentity.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_station, container, false)

        viewModel.getStation(stationIdentity)
        viewModel.station.observe(viewLifecycleOwner, {
            Log.d("STATION", it.toString())
            viewModel.getScheduleStations(it.id)
        })

        viewModel.scheduleStations.observe(viewLifecycleOwner, {
            if (it != null) {
                Log.d("SCHEDULE STATIONS", it.size.toString())

                val firstItem = it.first()
                Log.d("SCHEDULE STATION", firstItem.toString())
            }
        })

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(stationIdentity: Long) =
            StationFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PARAM1, stationIdentity)
                }
            }
    }
}