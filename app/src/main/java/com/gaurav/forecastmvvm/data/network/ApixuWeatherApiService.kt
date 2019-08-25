package com.gaurav.forecastmvvm.data.network

import com.gaurav.forecastmvvm.data.network.response.CurrentWeatherResponse
import com.gaurav.forecastmvvm.data.network.response.FutureWeatherResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


const val API_KEY = "3275ce2b2c294ba6b93201850190508"

//http://api.apixu.com/v1/current.json?key=3275ce2b2c294ba6b93201850190508&q=London

interface ApixuWeatherApiService {

    //call it to getting current weather with passing current location from call
    //and default language as english
    @GET("current.json")
    fun getCurrentWeather(
        //q -> query parameter for city
        @Query("q") location : String,
        @Query("lang") languageCode: String = "en"
    //cannot return direct current weather response as it take some time
    //so coroutine deferred use here
    ): Deferred<CurrentWeatherResponse>

    @GET("forecast.json")
    fun getFutureWeather(
        @Query("q") location: String,
        @Query("days") days: Int,
        @Query("lang") languageCode: String = "en"
    ): Deferred<FutureWeatherResponse>

    //we don't make the upper url as key bcoz it used in every single query
    //so instead we make it our url


    //to implement apixu weather service
    companion object{
        //on invoke function it create the apixu api service for our api_key
        //invoke func -> ApixuWeatherApiService()
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): ApixuWeatherApiService {
            //adding request to url or making our url use requestInterceptor
            val requestInterceptor = Interceptor{ chain ->
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("key", API_KEY)
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
                return@Interceptor chain.proceed(request)
            }
            //make the above url http client
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(connectivityInterceptor)
                .build()

            //simply return the real url address
            //http://api.apixu.com/v1/current.json?key=3275ce2b2c294ba6b93201850190508
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://api.apixu.com/v1/")
                    //as we use deferred
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApixuWeatherApiService::class.java)
        }

    }
}