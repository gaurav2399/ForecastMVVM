package com.gaurav.forecastmvvm.ui.weather.current

import android.util.Log
import androidx.lifecycle.ViewModel;
import com.gaurav.forecastmvvm.data.provider.UnitProvider
import com.gaurav.forecastmvvm.data.repository.ForecastRepository
import com.gaurav.forecastmvvm.internal.UnitSystem
import com.gaurav.forecastmvvm.internal.lazyDeferred
import com.gaurav.forecastmvvm.ui.base.WeatherViewModel
import java.net.SocketTimeoutException

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : WeatherViewModel(forecastRepository, unitProvider) {

    //as object of viewModel, created weather instantiating immediately
    //so we make it lazy so that it can wait for its implementation
    // then only it can instantiate

    val weather by lazyDeferred{
        try {
            forecastRepository.getCurrentWeather(super.isMetricUnit)
        }catch (e: SocketTimeoutException){
            throw e
        }

    }

}
