package com.example.autotrolejapp.line_variant

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.autotrolejapp.R
import com.example.autotrolejapp.database.AutotrolejDatabase
import com.example.autotrolejapp.entities.BusLocation
import com.example.autotrolejapp.entities.Line
import com.example.autotrolejapp.entities.Station
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class LineVariantFragment : Fragment() {
    private lateinit var lineVariantIds: ArrayList<String>
    private lateinit var selectedLineVariantId: String
    private lateinit var mMap: GoogleMap
    private var busLocationMarkers: MutableList<Marker> = mutableListOf()
    private var mapReady = false

    private val viewModel: LineVariantViewModel by lazy {
        val application = requireNotNull(this.activity).application
        val stationDatabaseDao = AutotrolejDatabase.getInstance(application).stationDatabaseDao
        val lineDatabaseDao = AutotrolejDatabase.getInstance(application).lineDatabaseDao
        val scheduleLineDatabaseDao = AutotrolejDatabase.getInstance(application).scheduleLineDatabaseDao
        val viewModelFactory = LineVariantViewModelFactory(stationDatabaseDao, lineDatabaseDao, scheduleLineDatabaseDao, application)
        ViewModelProvider(this, viewModelFactory).get(LineVariantViewModel::class.java)
    }

    private val stations: List<Station>
        get() = viewModel.stations.value.orEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_line_variant, container, false)

        readBundle(arguments)

        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        val rijekaLocation = LatLng(45.32,14.44)
        mapFragment.getMapAsync {
                googleMap -> mMap = googleMap
            mapReady = true
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rijekaLocation, 11f))
        }

        return view
    }

    private fun readBundle(bundle: Bundle?) {
        if (bundle != null) {
            lineVariantIds = bundle.getStringArrayList("lineVariantsIds") as ArrayList<String>
            selectedLineVariantId = lineVariantIds.first()

            //set observer on live stations (will be fetched through lineVariantId)
            viewModel.liveStations.observe(viewLifecycleOwner, { station ->
                updateMap(station)
            })

            //set observer on live busLines (will be fetched through busLocation.startId)
            viewModel.busLines.observe(viewLifecycleOwner, { busLine ->
                showBusLines(busLine)
            })

            //set observer on liveBusLocations
            viewModel.allBusesOnWantedLineLive.observe(viewLifecycleOwner, { busLocations ->
                if(!busLocations.isNullOrEmpty()) updateMapBusLocation(busLocations)
            })

            //set liveStations so observer can catch a change
            //TODO: na promjenu selectedLineVariantId kroz neki dropdown pozivat ovu funkciju opet
            viewModel.getLiveStations(selectedLineVariantId)

            //1a
            //viewModel.getBusLine(1304789)
            viewModel.getBusforWantedLine(selectedLineVariantId)
        }
    }

    private fun updateMapBusLocation(busLocations: List<BusLocation>) {
        Log.d("Iz updateMapBusLocation", "Uso u updejt map za bus")
        busLocationMarkers.forEach { marker ->
            marker.remove()
        }
        if (mapReady) {
            busLocations.forEach { busLocation ->
                val markerPos = LatLng(
                    busLocation.longitude!!.toDouble(),
                    busLocation.latitude!!.toDouble()
                )
                val markerName = busLocation.busName
                busLocationMarkers.add(mMap.addMarker(
                    MarkerOptions()
                        .position(markerPos)
                        .title(markerName)
                        .icon(bitMapFromVector(R.drawable.ic_red_bus))
                ))
            }
        }
    }

    private fun showBusLines(busLine: List<Line>?) {
        //Log.d("Iz showBusLines AAAA", busLine.toString())
    }


    private fun updateMap(stationsByLineVariant: List<Station>){
        if (mapReady) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getCenterPoint(stationsByLineVariant), 13.5f))
            stationsByLineVariant.forEach { station ->
                val markerPos = LatLng(
                    station.latitude!!.toDouble(),
                    station.longitude!!.toDouble()
                )
                val markerName = station.name
                mMap.addMarker(
                    MarkerOptions()
                        .position(markerPos)
                        .title(markerName)
                        .icon(bitMapFromVector(R.drawable.ic_bus_stop))
                )
            }
        }
    }

    private fun getCenterPoint (points: List<Station>): LatLng? {
        var latitude = 0.0
        var longitude = 0.0
        val n = points.size
        for (point in points) {
            latitude += point.latitude!!
            longitude += point.longitude!!
        }
        return LatLng(latitude / n, longitude / n)
    }

    private fun bitMapFromVector(vectorResID:Int): BitmapDescriptor {
        val vectorDrawable= ContextCompat.getDrawable(requireContext(),vectorResID)
        vectorDrawable!!.setBounds(0,0,vectorDrawable!!.intrinsicWidth,vectorDrawable.intrinsicHeight)
        val bitmap= Bitmap.createBitmap(vectorDrawable.intrinsicWidth,vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888)
        val canvas= Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance(lineVariantIds: List<String>) : LineVariantFragment? {
            val bundle = Bundle()
            bundle.putStringArrayList("lineVariantsIds", (ArrayList<String>(lineVariantIds)))
            val fragment = LineVariantFragment()
            fragment.arguments = bundle
            return fragment

        }
    }
}

