package com.example.autotrolejapp.helpers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.location.Location
import android.os.Handler
import android.os.SystemClock
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.autotrolejapp.R
import com.example.autotrolejapp.entities.BusLocation
import com.example.autotrolejapp.entities.Station
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

open class BaseFragment : Fragment() {

    protected lateinit var mMap: GoogleMap
    protected var mapReady = false

    protected var viewPortCentered = false

    protected var stationLocationMarkers: MutableList<Marker> = mutableListOf()
    protected var busLocationMarkers: MutableList<Marker> = mutableListOf()
    protected var currentLocationMarkers: MutableList<Marker> = mutableListOf()

    private val mapLocationZoom: Float = 15f

    protected val rijekaLatitude: Double
        get() {
            return 45.3318399
        }

    protected val rijekaLongitude: Double
        get() {
            return 14.4443319
        }

    protected val rijekaLocation: Location
        get() {
            val location = Location("rijekaLocation")
            location.latitude = rijekaLatitude
            location.longitude = rijekaLongitude
            return location
        }

    protected lateinit var locationClient: FusedLocationProviderClient
    protected open val mLocationCallback: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                if (location != null) {
                    val moveCamera = !currentLocationMarkers.any()
                    updateCurrentLocation(location, moveCamera)
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()

        locationClient.removeLocationUpdates(mLocationCallback)
    }

    override fun onResume() {
        super.onResume()

        setCurrentLocation()
    }

    protected fun setCurrentLocation(delayTimeInterval: Long = LocationHelper.defaultInterval) {
        currentLocationMarkers.forEach{ marker -> marker.remove() }
        locationClient = LocationHelper.getCurrentLocation(requireActivity(), requireContext(), mLocationCallback, delayTimeInterval)
    }

    protected fun initMap(mapFragment: SupportMapFragment) {
        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap
            mapReady = true

            val markerPos = LatLng(rijekaLatitude, rijekaLongitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPos, mapLocationZoom))
        }
    }

    private fun getMarkerOptions(position: LatLng, name: String, icon: Int): MarkerOptions {
        return MarkerOptions()
            .position(position)
            .title(name)
            .icon(bitMapFromVector(icon))
    }

    protected fun createMarker(latitude: Double, longitude: Double, name: String, icon: Int): Marker? {
        val markerPos = LatLng(latitude, longitude)
        return mMap.addMarker(getMarkerOptions(markerPos, name, icon))
    }

    protected fun updateMapStations(stations: List<Station>) {
        stationLocationMarkers.forEach { marker -> marker.remove() }

        if (mapReady && !stations.isNullOrEmpty()) {
            if (!viewPortCentered) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getCenterPoint(stations), 13f))
                viewPortCentered = true
            }

            val filteredStations: List<Station> = stations.filter { x -> x.isValid() };
            filteredStations.forEach { station ->
                val marker = createMarker(station.latitude, station.longitude, station.name, R.drawable.ic_bus_stop)
                if (marker != null) {
                    stationLocationMarkers.add(marker)
                }
            }
        }
    }

    private fun getCenterPoint (points: List<Station>): LatLng {
        var latitude = 0.0
        var longitude = 0.0
        val n = points.size

        for (point in points) {
            latitude += point.latitude
            longitude += point.longitude
        }
        return LatLng(latitude / n, longitude / n)
    }

    protected open fun updateMapBusLocation(busLocations: List<BusLocation>) {
        busLocationMarkers.forEach { marker -> marker.remove() }

        if (mapReady) {
            busLocations.forEach { busLocation ->
                val marker = createMarker(busLocation.latitude!!, busLocation.longitude!!, busLocation.busName, R.drawable.ic_red_bus)
                if (marker != null) {
                    busLocationMarkers.add(marker)
                }
            }
        }
    }

    private fun bitMapFromVector(vectorResID:Int): BitmapDescriptor {
        val vectorDrawable= ContextCompat.getDrawable(requireContext(),vectorResID)
        vectorDrawable!!.setBounds(0,0, vectorDrawable.intrinsicWidth,vectorDrawable.intrinsicHeight)
        val bitmap= Bitmap.createBitmap(vectorDrawable.intrinsicWidth,vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888)
        val canvas= Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    protected fun updateCurrentLocation(location: Location, moveCamera: Boolean = false) {
        currentLocationMarkers.forEach { marker -> marker.remove() }

        if (mapReady) {
            val marker = createMarker(location.latitude, location.longitude, "Me", R.drawable.ic_current_position)
            if (marker != null) {
                currentLocationMarkers.add(marker)

                if (moveCamera) {
                    val markerPos = LatLng(location.latitude, location.longitude)
                    animateMarker(marker, markerPos, false)
                }
            }
        }
    }

    protected fun animateMarker(
        marker: Marker, toPosition: LatLng,
        hideMarker: Boolean
    ) {
        val handler = Handler()

        val start = SystemClock.uptimeMillis()
        val proj: Projection = mMap.projection
        val startPoint: Point = proj.toScreenLocation(marker.position)
        val startLatLng: LatLng = proj.fromScreenLocation(startPoint)
        val duration: Long = 500
        val interpolator: Interpolator = LinearInterpolator()

        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val t: Float = interpolator.getInterpolation(
                    elapsed.toFloat()
                            / duration
                )
                val lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude
                val lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude
                marker.position = LatLng(lat, lng)

                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(lat, lng),
                        15.0f
                    )
                )

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                } else {
                    marker.isVisible = !hideMarker
                }
            }
        })
    }


}