package com.gaurav.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gaurav.forecastmvvm.data.provider.UnitProvider
import com.gaurav.forecastmvvm.data.repository.ForecastRepository


//viewModel are for preserving state
//as on destroying view model data not destroying
//so we don't need to create instance of these every time
//so we create one instance only at first launch then afterwards
//we get the already present instance not to create new one

class CurrentWeatherViewModelFactory(
    private val forecastRepository: ForecastRepository,
    private val unitProvider: UnitProvider
): ViewModelProvider.NewInstanceFactory(){

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CurrentWeatherViewModel(forecastRepository,unitProvider) as T
    }

}