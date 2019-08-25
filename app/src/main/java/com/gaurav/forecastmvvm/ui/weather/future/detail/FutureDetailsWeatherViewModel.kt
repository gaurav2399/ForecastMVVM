package com.gaurav.forecastmvvm.ui.weather.future.detail

import androidx.lifecycle.ViewModel;
import com.gaurav.forecastmvvm.data.provider.UnitProvider
import com.gaurav.forecastmvvm.data.repository.ForecastRepository
import com.gaurav.forecastmvvm.internal.lazyDeferred
import com.gaurav.forecastmvvm.ui.base.WeatherViewModel
import org.threeten.bp.LocalDate

class FutureDetailsWeatherViewModel(
    private val detailDate: LocalDate,
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : WeatherViewModel(forecastRepository, unitProvider) {

    val weather by lazyDeferred {
        forecastRepository.getFutureWeatherByDate(detailDate, super.isMetricUnit)
    }
}
