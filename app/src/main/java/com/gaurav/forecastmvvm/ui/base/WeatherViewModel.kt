package com.gaurav.forecastmvvm.ui.base

import androidx.lifecycle.ViewModel
import com.gaurav.forecastmvvm.data.provider.UnitProvider
import com.gaurav.forecastmvvm.data.repository.ForecastRepository
import com.gaurav.forecastmvvm.internal.UnitSystem
import com.gaurav.forecastmvvm.internal.lazyDeferred
import java.net.SocketTimeoutException

abstract class WeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
):ViewModel() {

    //get from settings later
    private  val unitSystem = unitProvider.getUnitSystem()

    val isMetricUnit: Boolean
        get() = unitSystem == UnitSystem.METRIC

    //as object of viewModel, created weather instantiating immediately
    //so we make it lazy so that it can wait for its implementation
    // then only it can instantiate

    val weatherLocation by lazyDeferred{
        try {
            forecastRepository.getWeatherLocation()
        }catch (e: SocketTimeoutException){
            throw e
        }

    }
}