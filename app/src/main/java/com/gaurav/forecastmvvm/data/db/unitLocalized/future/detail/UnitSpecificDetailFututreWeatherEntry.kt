package com.gaurav.forecastmvvm.data.db.unitLocalized.future.detail

import org.threeten.bp.LocalDate

interface UnitSpecificDetailFututreWeatherEntry {
    val date: LocalDate
    val maxTemperature: Double
    val minTemperature: Double
    val avgTemperature: Double
    val conditionText: String
    val conditionIconUrl: String
    val maxWindSpeed: Double
    val totalPrecipitation: Double
    val avgVisibilityDistance: Double
    val uv: Double
}