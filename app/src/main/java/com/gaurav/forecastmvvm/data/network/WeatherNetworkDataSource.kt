package com.gaurav.forecastmvvm.data.network

import androidx.lifecycle.LiveData
import com.gaurav.forecastmvvm.data.network.response.CurrentWeatherResponse
import com.gaurav.forecastmvvm.data.network.response.FutureWeatherResponse


//so that that we cannot get direct dta from service
//and also handle network exception issue

//interface as it is used fro DI
interface WeatherNetworkDataSource {

    val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
    val downloadedFutureWeather: LiveData<FutureWeatherResponse>

    //here int describe the status of fetching
    //that is fetched or not or any error
    suspend fun  fetchCurrentWeather(location: String,languageCode: String)

    suspend fun  fetchFutureWeather(location: String,languageCode: String)


}