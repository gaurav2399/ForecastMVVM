package com.gaurav.forecastmvvm.data.repository

import androidx.lifecycle.LiveData
import com.gaurav.forecastmvvm.data.db.entity.WeatherLocation
import com.gaurav.forecastmvvm.data.db.unitLocalized.current.UnitSpecificCurrentWeatherEntry
import com.gaurav.forecastmvvm.data.db.unitLocalized.future.detail.UnitSpecificDetailFututreWeatherEntry
import com.gaurav.forecastmvvm.data.db.unitLocalized.future.list.UnitSpecificSimpleFutureWeatherEntry
import org.threeten.bp.LocalDate


//Repository network and local data operation in a single place
//also used fro data caching
//we don't need to fetch data from network when app launches after this
//if local data up to date then we can use that


//interface-> layout for class what we want in our real class

interface ForecastRepository {

    suspend fun getCurrentWeather(metric: Boolean) : LiveData<out UnitSpecificCurrentWeatherEntry>

    suspend fun getFutureWeatherList(startDate: LocalDate, metric: Boolean): LiveData<out List<UnitSpecificSimpleFutureWeatherEntry>>

    suspend fun getFutureWeatherByDate(date: LocalDate, metric: Boolean): LiveData<out UnitSpecificDetailFututreWeatherEntry>

    suspend fun getWeatherLocation(): LiveData<WeatherLocation>

}