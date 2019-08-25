package com.gaurav.forecastmvvm.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gaurav.forecastmvvm.data.network.response.CurrentWeatherResponse
import com.gaurav.forecastmvvm.data.network.response.FutureWeatherResponse
import com.gaurav.forecastmvvm.data.provider.PreferenceProvider
import com.gaurav.forecastmvvm.internal.NoConnectivityException
import retrofit2.HttpException
import java.net.SocketTimeoutException

const val SUCCESS = 1
const val NO_INTERNET = 2
const val HTTP_FAIL =3

const val FORECAST_DAYS_COUNT = 7

class WeatherNetworkDataSourceImpl(
    private val apixuWeatherApiService: ApixuWeatherApiService)
    : WeatherNetworkDataSource {

    private val _downloadedCurrentWeather = MutableLiveData<CurrentWeatherResponse>()

    override val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
        get() = _downloadedCurrentWeather

    override suspend fun fetchCurrentWeather(location: String, languageCode: String){
        try{
            val fetchedCurrentUser = apixuWeatherApiService
                .getCurrentWeather(location,languageCode)
                .await()

            //here we have to update download current weather
            //live data cannot be change
            //only mutable live data can be changed

            _downloadedCurrentWeather.postValue(fetchedCurrentUser)

        }catch (e: NoConnectivityException){
            Log.e("Connectivity","No internet Connection",e)
        }catch(e: HttpException){
            Log.e("Handling http exception","done")
            throw HttpException(e.response())
        }catch (e: SocketTimeoutException){
            throw SocketTimeoutException()
        }

    }

    private val _downloadedFutureWeather = MutableLiveData<FutureWeatherResponse>()

    override val downloadedFutureWeather: LiveData<FutureWeatherResponse>
        get() = _downloadedFutureWeather

    override suspend fun fetchFutureWeather(location: String, languageCode: String) {
        try{
            val fetchedFutureWeather = apixuWeatherApiService
                .getFutureWeather(location, FORECAST_DAYS_COUNT,languageCode)
                .await()

            //here we have to update download current weather
            //live data cannot be change
            //only mutable live data can be changed

            _downloadedFutureWeather.postValue(fetchedFutureWeather)

        }catch (e: NoConnectivityException){
            Log.e("Connectivity","No internet Connection",e)
        }catch(e: HttpException){
            Log.e("Handling http exception","done")
            throw HttpException(e.response())
        }catch (e: SocketTimeoutException){
            throw SocketTimeoutException()
        }

    }


}