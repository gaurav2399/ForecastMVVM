package com.gaurav.forecastmvvm.ui

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest

class LifecycleBoundLocationManager(
    lifecycleOwner: LifecycleOwner,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val locationCallback: LocationCallback
): LifecycleObserver{
    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private val locationRequest = LocationRequest().apply {
        //every count at which location request
        interval=5000
        fastestInterval=5000
        //how accurate location we have
        //fro more we can use high accuracy but
        // with fine location access permission also
        priority=LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun removeLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}
