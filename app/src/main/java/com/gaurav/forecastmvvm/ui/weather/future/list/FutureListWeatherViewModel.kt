package com.gaurav.forecastmvvm.ui.weather.future.list

import androidx.lifecycle.ViewModel;
import com.gaurav.forecastmvvm.data.provider.UnitProvider
import com.gaurav.forecastmvvm.data.repository.ForecastRepository
import com.gaurav.forecastmvvm.internal.lazyDeferred
import com.gaurav.forecastmvvm.ui.base.WeatherViewModel
import org.threeten.bp.LocalDate

class FutureListWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    private val unitProvider: UnitProvider
) : WeatherViewModel(forecastRepository, unitProvider) {

    //as object of viewModel, created weather instantiating immediately
    //so we make it lazy so that it can wait for its implementation
    // then only it can instantiate

    val weatherEntries by lazyDeferred{
        forecastRepository.getFutureWeatherList(LocalDate.now(), super.isMetricUnit)
    }

}
