package com.example.autotrolejapp.helpers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

abstract class LocationHelper {

    companion object {

        val defaultInterval: Long = 30 * 1000

        fun checkLocationPermission(context: Context): Boolean {
            if (context.let {
                    ActivityCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } != PackageManager.PERMISSION_GRANTED && context.let {
                    ActivityCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                } != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }

            return true
        }

        fun requestLocationPermission(activity: Activity) {
            requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        private fun getLocationRequest(interval: Long = defaultInterval, fastestInterval: Long = 0, priority: Int = LocationRequest.PRIORITY_HIGH_ACCURACY): LocationRequest {
            val mLocationRequest = LocationRequest.create()
            mLocationRequest.interval = interval
            mLocationRequest.fastestInterval = fastestInterval
            mLocationRequest.priority = priority
            return mLocationRequest
        }

        fun getCurrentLocation(activity: Activity, context: Context, mLocationCallback: LocationCallback, interval: Long = defaultInterval): FusedLocationProviderClient {
            if (!checkLocationPermission(context)) {
                requestLocationPermission(activity)
            }

            val mLocationRequest = getLocationRequest(interval)
            val locationClient = LocationServices.getFusedLocationProviderClient(context)
            locationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)

            return locationClient
        }
    }
}