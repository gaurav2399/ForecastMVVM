package com.gaurav.forecastmvvm.data.db

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

object LocalDateConverter {
    @TypeConverter
    //jvm static is used to annotate that it is really static method
    @JvmStatic
    //let used for that function call is from not null side

    fun stringToDate(str: String?) = str?.let {
        //ISO_LOCAL_DATE is the format used by api
        //differ by diff apis
        LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @TypeConverter
    @JvmStatic
    fun dateToString(dateTime: LocalDate?) = dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE)
}