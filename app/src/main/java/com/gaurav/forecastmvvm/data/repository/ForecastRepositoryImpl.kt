package com.gaurav.forecastmvvm.data.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.gaurav.forecastmvvm.data.db.CurrentWeatherDao
import com.gaurav.forecastmvvm.data.db.FutureWeatherDao
import com.gaurav.forecastmvvm.data.db.WeatherLocationDao
import com.gaurav.forecastmvvm.data.db.entity.WeatherLocation
import com.gaurav.forecastmvvm.data.db.unitLocalized.current.UnitSpecificCurrentWeatherEntry
import com.gaurav.forecastmvvm.data.db.unitLocalized.future.detail.UnitSpecificDetailFututreWeatherEntry
import com.gaurav.forecastmvvm.data.db.unitLocalized.future.list.UnitSpecificSimpleFutureWeatherEntry
import com.gaurav.forecastmvvm.data.network.FORECAST_DAYS_COUNT
import com.gaurav.forecastmvvm.data.network.WeatherNetworkDataSource
import com.gaurav.forecastmvvm.data.network.response.CurrentWeatherResponse
import com.gaurav.forecastmvvm.data.network.response.FutureWeatherResponse
import com.gaurav.forecastmvvm.data.provider.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.*

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val futureWeatherDao: FutureWeatherDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    private val locationProvider: LocationProvider
    ) : ForecastRepository {

    init {
        //as repository has no lifecycle so use observeForever
        weatherNetworkDataSource.apply {
            downloadedCurrentWeather.observeForever { newCurrentWeather ->
                persistFetchedCurrentWeather(newCurrentWeather)
            }
            downloadedFutureWeather.observeForever { newFutureWeather ->
                persistFetchedFutureWeather(newFutureWeather)
            }
        }
    }


    //out means that we can also return which can only implemnt UnitSp...
    //and not only directly UnitSpe..
    override suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry> {

        try {
            return withContext(Dispatchers.IO) {
                initWeatherData()
                return@withContext if (metric) currentWeatherDao.getWeatherMetric()
                else currentWeatherDao.getWeatherMetric()
            }
        }catch (e: SocketTimeoutException){
            throw SocketTimeoutException()
        }


    }

    override suspend fun getFutureWeatherList(
        startDate: LocalDate,
        metric: Boolean
    ): LiveData<out List<UnitSpecificSimpleFutureWeatherEntry>> {
        try {
            return withContext(Dispatchers.IO) {
                initWeatherData()
                return@withContext if (metric) futureWeatherDao.getSimpleWeatherForecastsMetric(startDate)
                else futureWeatherDao.getSimpleWeatherForecastsImperial(startDate)
            }
        }catch (e: SocketTimeoutException){
            throw SocketTimeoutException()
        }
    }

    override suspend fun getFutureWeatherByDate(
        date: LocalDate,
        metric: Boolean
    ): LiveData<out UnitSpecificDetailFututreWeatherEntry> {
        return withContext(Dispatchers.IO){
            return@withContext if(metric) futureWeatherDao.getDetailedWeatherByDateMetric(date)
            else futureWeatherDao.getDetailedWeatherByDateImperial(date)
        }
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        try {
            return withContext(Dispatchers.IO) {
                initWeatherData()
                return@withContext weatherLocationDao.getLocation()
            }
        }catch (e: SocketTimeoutException){
            throw SocketTimeoutException()
        }
    }

    //we can use global scope in repository as it has no lifecycle
    //but not in activities and fragments
    //as if condition rises when lifecycle destroy but thread still running it create exception
    //this is not done in repository as it has no sense of destroy

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry)
            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }

    private fun persistFetchedFutureWeather(fetchedWeather: FutureWeatherResponse) {

        fun deleteOldForecastData() {
            val today = LocalDate.now()
            futureWeatherDao.deleteOldEntries(today)
        }

        GlobalScope.launch(Dispatchers.IO) {
            deleteOldForecastData()
            val futureWeatherList = fetchedWeather.futureWeatherEntries.entries
            futureWeatherDao.insert(futureWeatherList)
            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }

    //here we can find we need to fetch data or not
    private suspend fun initWeatherData() {

        //getLocation returning LiveData which is not really synchronous
        //so we get always null from value getter
        val lastWeatherLocation = weatherLocationDao.getLocationNonLive()

        try{
            if (lastWeatherLocation == null
                || locationProvider.hasLocationChanged(lastWeatherLocation)
            ) {
                fetchCurrentWeather()
                fetchFutureWeather()
            }
            if (isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime))
                fetchCurrentWeather()

            if (isFetchFutureNeeded())
                    fetchFutureWeather()

        }catch (e: SocketTimeoutException){
            throw SocketTimeoutException()
        }
    }


    private suspend fun fetchCurrentWeather() {
        try {
            return weatherNetworkDataSource.fetchCurrentWeather(
                locationProvider.getPreferredLocationString(),
                Locale.getDefault().language
            )
        }catch (e: HttpException){
            locationProvider.setDefaultValues()
        }catch (e: SocketTimeoutException){
            throw SocketTimeoutException()
        }catch (e: Exception){
            Log.e("Exception handling","done")
        }

    }

    private suspend fun fetchFutureWeather() {
        try {
           return weatherNetworkDataSource.fetchFutureWeather(
                locationProvider.getPreferredLocationString(),
                Locale.getDefault().language
            )
        }catch (e: HttpException){
            locationProvider.setDefaultValues()
        }catch (e: SocketTimeoutException){
            throw SocketTimeoutException()
        }
    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }

    private fun isFetchFutureNeeded(): Boolean {
        val today = LocalDate.now()
        val futureWeatherCount = futureWeatherDao.countFutureWeather(today)
        return futureWeatherCount < FORECAST_DAYS_COUNT
    }
}