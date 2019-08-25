package com.gaurav.forecastmvvm.data.network.response

import com.gaurav.forecastmvvm.data.db.entity.CurrentWeatherEntry
import com.gaurav.forecastmvvm.data.db.entity.WeatherLocation
import com.google.gson.annotations.SerializedName


data class CurrentWeatherResponse(
    @SerializedName("current")
    val currentWeatherEntry: CurrentWeatherEntry,
    val location: WeatherLocation
)