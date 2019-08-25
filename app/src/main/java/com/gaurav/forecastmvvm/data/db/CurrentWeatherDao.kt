package com.gaurav.forecastmvvm.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gaurav.forecastmvvm.data.db.entity.CURRENT_WEATHER_ID
import com.gaurav.forecastmvvm.data.db.entity.CurrentWeatherEntry
import com.gaurav.forecastmvvm.data.db.unitLocalized.current.ImperialCurrentWeatherEntry
import com.gaurav.forecastmvvm.data.db.unitLocalized.current.MetricCurrentWeatherEntry


//dao always be interface
//dao are made to access db whereas entity is table
@Dao
interface CurrentWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(weatherEntry: CurrentWeatherEntry)

    @Query("select * from current_weather where id = $CURRENT_WEATHER_ID")
    fun getWeatherMetric(): LiveData<MetricCurrentWeatherEntry>

    @Query("select * from current_weather where id = $CURRENT_WEATHER_ID")
    fun getWeatherImperial(): LiveData<ImperialCurrentWeatherEntry>
}