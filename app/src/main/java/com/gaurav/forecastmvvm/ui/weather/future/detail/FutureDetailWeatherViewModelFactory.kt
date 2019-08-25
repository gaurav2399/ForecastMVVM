package com.gaurav.forecastmvvm.ui.weather.future.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gaurav.forecastmvvm.data.provider.UnitProvider
import com.gaurav.forecastmvvm.data.repository.ForecastRepository
import org.threeten.bp.LocalDate

class FutureDetailWeatherViewModelFactory(
    private val detailDate: LocalDate,
    private val forecastRepository: ForecastRepository,
    private val unitProvider: UnitProvider
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FutureDetailsWeatherViewModel(detailDate, forecastRepository, unitProvider) as T
    }
}