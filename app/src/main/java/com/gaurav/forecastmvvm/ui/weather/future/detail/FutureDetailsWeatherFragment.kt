package com.gaurav.forecastmvvm.ui.weather.future.detail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

import com.gaurav.forecastmvvm.R
import com.gaurav.forecastmvvm.data.db.LocalDateConverter
import com.gaurav.forecastmvvm.internal.DateNotFoundException
import com.gaurav.forecastmvvm.internal.glide.GlideApp
import com.gaurav.forecastmvvm.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.future_details_weather_fragment.*
import kotlinx.android.synthetic.main.future_details_weather_fragment.textView_condition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class FutureDetailsWeatherFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()

    //factory of factory as we don't have date yet we have to pass it to kodein factory
    private val viewModelFactoryInstanceFactory
            : ((LocalDate) -> FutureDetailWeatherViewModelFactory) by factory()

    private lateinit var viewModel: FutureDetailsWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.future_details_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val safeArgs = arguments?.let { FutureDetailsWeatherFragmentArgs.fromBundle(it) }
        val date = LocalDateConverter.stringToDate(safeArgs?.dateString) ?: throw DateNotFoundException()
        viewModel = ViewModelProviders.of(this, viewModelFactoryInstanceFactory(date)).get(FutureDetailsWeatherViewModel::class.java)

        bindUI()

    }

    private fun bindUI() = launch(Dispatchers.Main) {
        val futureWeather = viewModel.weather.await()
        val weatherLocation = viewModel.weatherLocation.await()

        weatherLocation.observe(this@FutureDetailsWeatherFragment, Observer { location ->
            if (location == null) return@Observer
            updateLocation(location.name)
        })

        futureWeather.observe(this@FutureDetailsWeatherFragment, Observer { weatherEntry ->
            if (weatherEntry == null) return@Observer

            updateDate(weatherEntry.date)
            updateTemperatures(weatherEntry.avgTemperature,
                weatherEntry.minTemperature, weatherEntry.maxTemperature)
            updateCondition(weatherEntry.conditionText)
            updatePrecipitation(weatherEntry.totalPrecipitation)
            updateWindSpeed(weatherEntry.maxWindSpeed)
            updateVisibility(weatherEntry.avgVisibilityDistance)
            updateUv(weatherEntry.uv)

            GlideApp.with(this@FutureDetailsWeatherFragment)
                .load("http:" + weatherEntry.conditionIconUrl)
                .into(imageView_condition_icon_detail_item)
        })
    }

    private fun chooseLocalizedUnitAbbreviation(metric: String, imperial: String): String {
        return if (viewModel.isMetricUnit) metric else imperial
    }

    private fun updateLocation(location: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
    }

    private fun updateDate(date: LocalDate) {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle =
            date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
    }

    private fun updateTemperatures(temperature: Double, min: Double, max: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("°C", "°F")
        textView_temperature.text = "$temperature$unitAbbreviation"
        textView_min_max_temperature.text = "Min: $min$unitAbbreviation, Max: $max$unitAbbreviation"
    }

    private fun updateCondition(condition: String) {
        textView_condition.text = condition
    }

    private fun updatePrecipitation(precipitationVolume: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("mm", "in")
        textView_precipitation.text = "Precipitation: $precipitationVolume $unitAbbreviation"
    }

    private fun updateWindSpeed(windSpeed: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("kph", "mph")
        textView_wind.text = "Wind speed: $windSpeed $unitAbbreviation"
    }

    private fun updateVisibility(visibilityDistance: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("km", "mi.")
        textView_visibility.text = "Visibility: $visibilityDistance $unitAbbreviation"
    }

    private fun updateUv(uv: Double) {
        textView_uv.text = "UV: $uv"
    }

}
