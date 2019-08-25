package com.gaurav.forecastmvvm.data.provider

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

abstract class PreferenceProvider(context: Context) {
    private val appContext = context.applicationContext


    protected val prefernces: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)
}