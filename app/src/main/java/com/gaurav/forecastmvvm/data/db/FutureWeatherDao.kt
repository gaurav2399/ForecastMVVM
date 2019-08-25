package com.gaurav.forecastmvvm.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gaurav.forecastmvvm.data.db.entity.FutureWeatherEntry
import com.gaurav.forecastmvvm.data.db.unitLocalized.future.detail.ImperialDetailFutureWeatherEntry
import com.gaurav.forecastmvvm.data.db.unitLocalized.future.detail.MetricDetailFutureWeatherEntry
import com.gaurav.forecastmvvm.data.db.unitLocalized.future.list.ImperialSimpleFutureWeatherEntry
import com.gaurav.forecastmvvm.data.db.unitLocalized.future.list.MetricSimpleFutureWeatherEntry
import org.threeten.bp.LocalDate


@Dao
interface FutureWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(futureWeatherEntries: List<FutureWeatherEntry>)

    //date function convert data into date evne it of localdate format or of string

    @Query("select * from future_weather where date(date) >= date(:startDate)")
    fun getSimpleWeatherForecastsMetric(startDate: LocalDate): LiveData<List<MetricSimpleFutureWeatherEntry>>

    @Query("select * from future_weather where date(date) >= date(:startDate)")
    fun getSimpleWeatherForecastsImperial(startDate: LocalDate): LiveData<List<ImperialSimpleFutureWeatherEntry>>


    @Query("select * from future_weather where date(date) = date(:date)")
    fun getDetailedWeatherByDateMetric(date: LocalDate): LiveData<MetricDetailFutureWeatherEntry>

    @Query("select * from future_weather where date(date) = date(:date)")
    fun getDetailedWeatherByDateImperial(date: LocalDate): LiveData<ImperialDetailFutureWeatherEntry>

    //it return count of future weather entries
    @Query("select count(id) from future_weather where date(date) >= date(:startDate)")
    fun countFutureWeather(startDate: LocalDate): Int

    //delete unnecessary stuff
    @Query("delete from future_weather where date(date) < date(:firstDateToKeep)")
    fun deleteOldEntries(firstDateToKeep: LocalDate)
}