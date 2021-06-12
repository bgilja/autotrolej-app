package com.example.autotrolejapp.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.autotrolejapp.MainActivity
import com.example.autotrolejapp.R
import com.example.autotrolejapp.database.AutotrolejDatabase
import com.example.autotrolejapp.entities.Station
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class MapFragment : Fragment(){
    private lateinit var mMap: GoogleMap
    private var mapReady = false

    private var busLocationMarkers: MutableList<Marker> = mutableListOf()
    private var currentLocationMarkers: MutableList<Marker> = mutableListOf()

    private val rijekaLocation: Location
        get() {
            val location = Location("rijekaLocation")
            location.latitude = 45.3318399
            location.longitude = 14.4443319
            return location
        }

    private val viewModel: MapViewModel by lazy {
        val application = requireNotNull(this.activity).application
        val stationDatabaseDao = AutotrolejDatabase.getInstance(application).stationDatabaseDao
        val viewModelFactory = MapViewModelFactory(stationDatabaseDao, application)
        ViewModelProvider(this, viewModelFactory).get(MapViewModel::class.java)
    }

    private val stations: List<Station>
        get() = viewModel.stations.value.orEmpty()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment =  childFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap
            mapReady = true
            // updateCurrentLocation(rijekaLocation, true)
        }

        checkLocationPermission()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stations.observe(viewLifecycleOwner, {
            updateMap()
        })

        viewModel.busLocations.observe(viewLifecycleOwner, {
            updateMapBusLocation()
        })
    }

    private fun checkLocationPermission() {
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        val mLocationRequest = LocationRequest.create()
        mLocationRequest.interval = 30 * 1000
        mLocationRequest.fastestInterval = 0
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                var locationIsSet = false
                for (location in locationResult.locations) {
                    if (location != null) {
                        val moveCamera = !currentLocationMarkers.any()
                        updateCurrentLocation(location, moveCamera)
                        locationIsSet = true
                    }
                }

                if (!locationIsSet) {
                    updateCurrentLocation(rijekaLocation)
                }
            }
        }

        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    private fun updateMap(){
        if (mapReady && !stations.isNullOrEmpty()) {
            var filteredStations: List<Station> = stations.filter { x -> x.isValid() };
            filteredStations.forEach { station ->
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

    private fun updateCurrentLocation(location: Location, moveCamera: Boolean = false) {
        currentLocationMarkers.forEach { marker ->
            marker.remove()
        }

        val markerPos = LatLng(
            location.latitude,
            location.longitude
        )

        if (moveCamera) mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPos, 14f))

        if (mapReady) {
            currentLocationMarkers.add(mMap.addMarker(
                MarkerOptions()
                    .position(markerPos)
                    .title("Me")
                    .icon(bitMapFromVector(R.drawable.ic_current_position))
            ))
        }
    }

    private fun updateMapBusLocation() {
        busLocationMarkers.forEach { marker ->
            marker.remove()
        }

        if (mapReady) {
            viewModel.busLocations.value?.forEach { busLocation ->
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

    private fun bitMapFromVector(vectorResID:Int):BitmapDescriptor {
        val vectorDrawable=ContextCompat.getDrawable(requireContext(),vectorResID)
        vectorDrawable!!.setBounds(0,0,vectorDrawable!!.intrinsicWidth,vectorDrawable.intrinsicHeight)
        val bitmap=Bitmap.createBitmap(vectorDrawable.intrinsicWidth,vectorDrawable.intrinsicHeight,Bitmap.Config.ARGB_8888)
        val canvas=Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    companion object {
        const val FRAGMENT_ID: Int = 2

        @JvmStatic
        fun newInstance() =
            MapFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}