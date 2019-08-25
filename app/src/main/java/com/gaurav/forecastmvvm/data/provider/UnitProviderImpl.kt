package com.gaurav.forecastmvvm.data.provider

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.gaurav.forecastmvvm.internal.UnitSystem

class UnitProviderImpl(context: Context) : PreferenceProvider(context), UnitProvider {

    override fun getUnitSystem(): UnitSystem {
        val selectedName = prefernces.getString("UNIT_SYSTEM", UnitSystem.METRIC.name)
        return UnitSystem.valueOf(selectedName!!)
    }
}