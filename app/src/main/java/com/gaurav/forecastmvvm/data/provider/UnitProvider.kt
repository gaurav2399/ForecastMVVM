package com.gaurav.forecastmvvm.data.provider

import com.gaurav.forecastmvvm.internal.UnitSystem

interface UnitProvider {
    fun getUnitSystem(): UnitSystem
}