package com.gaurav.forecastmvvm.data.provider

import com.gaurav.forecastmvvm.data.db.entity.WeatherLocation

interface LocationProvider {
    suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean
    suspend fun getPreferredLocationString(): String
    suspend fun setDefaultValues()
}