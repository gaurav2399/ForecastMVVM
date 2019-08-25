package com.gaurav.forecastmvvm

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import android.widget.Toast
import com.gaurav.forecastmvvm.data.db.ForecastDatabase
import com.gaurav.forecastmvvm.data.network.*
import com.gaurav.forecastmvvm.data.provider.LocationProvider
import com.gaurav.forecastmvvm.data.provider.LocationProviderImpl
import com.gaurav.forecastmvvm.data.provider.UnitProvider
import com.gaurav.forecastmvvm.data.provider.UnitProviderImpl
import com.gaurav.forecastmvvm.data.repository.ForecastRepository
import com.gaurav.forecastmvvm.data.repository.ForecastRepositoryImpl
import com.gaurav.forecastmvvm.ui.weather.current.CurrentWeatherViewModelFactory
import com.gaurav.forecastmvvm.ui.weather.future.detail.FutureDetailWeatherViewModelFactory
import com.gaurav.forecastmvvm.ui.weather.future.list.FutureListWeatherViewModelFactory
import com.google.android.gms.location.LocationServices
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.*
import org.threeten.bp.LocalDate

class ForecastApplication : Application(), KodeinAware {
    override val kodein: Kodein = Kodein.lazy {

        //it can provide instance of context and services
        import(androidXModule(this@ForecastApplication))


        //here instance is from import statement so we can get context
        bind() from singleton { ForecastDatabase(instance()) }
        bind() from singleton { instance<ForecastDatabase>().currentWeatherDao() }
        bind() from singleton { instance<ForecastDatabase>().futureWeatherDao() }
        bind() from singleton { instance<ForecastDatabase>().weatherLocationDao() }

        //for interfaces
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { ApixuWeatherApiService(instance()) }
        bind<WeatherNetworkDataSource>() with singleton { WeatherNetworkDataSourceImpl(instance()) }
        bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }
        bind<LocationProvider>() with singleton { LocationProviderImpl(instance(), instance()) }
        bind<ForecastRepository>() with singleton { ForecastRepositoryImpl(instance(), instance(), instance(),instance(), instance()) }
        bind<UnitProvider>() with singleton { UnitProviderImpl(instance()) }
        bind() from provider { CurrentWeatherViewModelFactory(instance(), instance()) }
        bind() from provider { FutureListWeatherViewModelFactory(instance(), instance()) }
        // to handling variable data we use that type of syntax here detail date is varying
        bind() from factory{ detailDate: LocalDate -> FutureDetailWeatherViewModelFactory(detailDate, instance(), instance())}


    }

    //as we are using zoned time of ThreeTen
    //so we have to initialise it

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        //it handle to set default values when first open
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }

    fun showNotLocationToast(msg: String){
        Toast.makeText(applicationContext,msg,Toast.LENGTH_LONG).show()
    }
}