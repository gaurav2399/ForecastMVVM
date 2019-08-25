package com.gaurav.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

import com.gaurav.forecastmvvm.R
import com.gaurav.forecastmvvm.internal.glide.GlideApp
import com.gaurav.forecastmvvm.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.net.SocketTimeoutException
import kotlin.Exception

class CurrentWeatherFragment : ScopedFragment(),KodeinAware {

    //closest kodein is from application
    //we can also have local kodein which can override some  functionality
    override val kodein by closestKodein()
    //getting instance from DI
    private val viewModelFactory: CurrentWeatherViewModelFactory by instance()
    //we can get rid of new instance as we implement kodein
    //not need every time new instance

    private lateinit var viewModel: CurrentWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.current_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = ""
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = ""
        group_no_internet.visibility=View.INVISIBLE
        viewModel = ViewModelProviders.of(this,viewModelFactory).get(CurrentWeatherViewModel::class.java)

        bindUI()
        retry_btn.setOnClickListener {
            group_no_internet.visibility=View.INVISIBLE
            viewModel = ViewModelProviders.of(this,viewModelFactory).get(CurrentWeatherViewModel::class.java)
            bindUI()
        }
    }

    private fun bindUI() = launch{
        try {
            val currentWeather = viewModel.weather.await()

            val weatherLocation = viewModel.weatherLocation.await()

            weatherLocation.observe(this@CurrentWeatherFragment, Observer { location ->
                if (location == null) return@Observer

                updateLocation(location.name)

            })
            currentWeather.observe(this@CurrentWeatherFragment, Observer {
                if (it == null) return@Observer

                group_loading.visibility = View.GONE
                updateDateToToday()
                updateTemperatures(it.temperature, it.feelsLikeTemperature)
                updateCondition(it.conditionText)
                updatePercipitation(it.precipitationVolume)
                updateWind(it.windDirection, it.windSpeed)
                updateVisibility(it.visibilityDistance)

                GlideApp.with(this@CurrentWeatherFragment)
                    .load("http:${it.conditionIconUrl}")
                    .into(imageView_condition_icon)
            })
        }catch (e: SocketTimeoutException){
            Log.e("exception catches ","success")
            group_loading.visibility=View.GONE
            group_no_internet.visibility=View.VISIBLE
        }
    }




    private fun chooseLocalisedUnit(metric: String,imperial: String): String{
        return if(viewModel.isMetricUnit) metric else imperial
    }
    //MANAGING TOOLBAR
    private fun updateLocation(location: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
    }

    private fun updateDateToToday(){
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Today"
    }

    private fun updateTemperatures(temperature: Double, feelsLikeTemp: Double){
        val unitAbbreviation = chooseLocalisedUnit("°C","°F")
        textView_temperature.text = "$temperature$unitAbbreviation"
        textView_feels_like_temperature.text = "Feels like $feelsLikeTemp$unitAbbreviation"
    }

    private fun updateCondition(condition: String){
        textView_condition.text = condition
    }

    private fun updatePercipitation(percipitationVolume: Double){
        val unitAbbreviation = chooseLocalisedUnit("mm","inches")
        textView_precipitation.text = "Percipitation: $percipitationVolume $unitAbbreviation"
    }

    private fun updateWind(windDirection: String, windSpeed: Double) {
        val unitAbbreviation = chooseLocalisedUnit("kph", "mph")
        textView_wind.text = "Wind: $windDirection, $windSpeed $unitAbbreviation"
    }

    private fun updateVisibility(visibilityDistance: Double) {
        val unitAbbreviation = chooseLocalisedUnit("km", "mi.")
        textView_visibility.text = "Visibility: $visibilityDistance $unitAbbreviation"
    }
}
