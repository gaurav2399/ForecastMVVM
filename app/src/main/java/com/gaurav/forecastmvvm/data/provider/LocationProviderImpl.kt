package com.gaurav.forecastmvvm.data.provider

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.gaurav.forecastmvvm.ForecastApplication
import com.gaurav.forecastmvvm.data.db.entity.WeatherLocation
import com.gaurav.forecastmvvm.internal.LocationPermissionNotGrantedException
import com.gaurav.forecastmvvm.internal.asDeferred
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Deferred
import kotlin.math.abs

const val USE_DEVICE_LOCATION = "USE_DEVICE_LOCATION"
const val CUSTOM_LOCATION = "CUSTOM_LOCATION"

class LocationProviderImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    context: Context
) : PreferenceProvider(context), LocationProvider {


    private val appContext = context.applicationContext
    override suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        val deviceLocationChanged = try {
            hasDeviceLocationChanged(lastWeatherLocation)
        } catch (e: LocationPermissionNotGrantedException) {
            false
        }
        return deviceLocationChanged || hasCustomLocationChanged(lastWeatherLocation)
    }


    override suspend fun getPreferredLocationString(): String {
        if (isUsingDeviceLocation()) {
            try {
                val deviceLocation = getLastDeviceLocation().await() ?: return "${getCustomLocationName()}"
                return "${deviceLocation.latitude},${deviceLocation.longitude}"
            } catch (e: LocationPermissionNotGrantedException) {
                return "${getCustomLocationName()}"
            }
        }else
            return "${getCustomLocationName()}"

    }

    override suspend fun setDefaultValues() {
        //ForecastApplication().showNotLocationToast("Please enter a valid location")
        //Toast.makeText(appContext,"Please enter a valid location",Toast.LENGTH_LONG).show()
        prefernces.edit().putString(CUSTOM_LOCATION,"Delhi").apply()


    }

    private suspend fun hasDeviceLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        if (!isUsingDeviceLocation())
            return false

        val deviceLocation = getLastDeviceLocation().await()
            ?: return false

        //Comparing doubles cannot be done with "=="
        val comparisonThreshold = 0.01
        //describing that at what decimal point we referred values
        return abs(deviceLocation.latitude - lastWeatherLocation.lat) > comparisonThreshold &&
                abs(deviceLocation.longitude - lastWeatherLocation.lon) > comparisonThreshold
    }

    private fun hasCustomLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        if(!isUsingDeviceLocation()) {
            val customLocationName = getCustomLocationName()
            return customLocationName != lastWeatherLocation.name
        }
        return false
    }

    private fun getCustomLocationName(): String? {
        return prefernces.getString(CUSTOM_LOCATION, null)
    }

    private fun isUsingDeviceLocation(): Boolean {
        return prefernces.getBoolean(USE_DEVICE_LOCATION, true)
    }


    //to use fused location first add permission to manifest
    // and then manually check user give access or not
    @SuppressLint("MissingPermission")
    private fun getLastDeviceLocation(): Deferred<Location?> {
        return if (hasLocationPermission())
        //converting task to deferred

        //at some instance there is no last location
        // so it gives null so we have to make sure
        // that it gives some kind of location always
        // we handle it in main activity
            fusedLocationProviderClient.lastLocation.asDeferred()
        else
            throw LocationPermissionNotGrantedException()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

}