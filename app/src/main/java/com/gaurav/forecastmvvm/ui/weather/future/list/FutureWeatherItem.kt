package com.gaurav.forecastmvvm.ui.weather.future.list

import com.gaurav.forecastmvvm.R
import com.gaurav.forecastmvvm.data.db.unitLocalized.future.list.MetricSimpleFutureWeatherEntry
import com.gaurav.forecastmvvm.data.db.unitLocalized.future.list.UnitSpecificSimpleFutureWeatherEntry
import com.gaurav.forecastmvvm.internal.glide.GlideApp
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_future_weather.*
import kotlinx.android.synthetic.main.item_future_weather.textView_condition
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class FutureWeatherItem(
    val weatherEntry: UnitSpecificSimpleFutureWeatherEntry
): Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            textView_condition.text = weatherEntry.conditionText
            updateDate()
            updateTemperature()
            updateConditionImage()
        }
    }

    override fun getLayout() = R.layout.item_future_weather

    private fun ViewHolder.updateDate() {
        //apply medium format as we want only jan instead of january
        val dtFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        textView_date.text = weatherEntry.date.format(dtFormatter)
    }

    private fun ViewHolder.updateTemperature() {
        val unitAbbreviation = if (weatherEntry is MetricSimpleFutureWeatherEntry) "°C"
        else "°F"
        textView_temperature_item.text = "${weatherEntry.avgTemperature}$unitAbbreviation"
    }

    private fun ViewHolder.updateConditionImage() {
        GlideApp.with(this.containerView)
            .load("http:" + weatherEntry.conditionIconUrl)
            .into(imageView_condition_icon_item)
    }
}