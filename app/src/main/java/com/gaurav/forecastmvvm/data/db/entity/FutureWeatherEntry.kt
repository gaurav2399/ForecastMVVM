package com.gaurav.forecastmvvm.data.db.entity


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gaurav.forecastmvvm.data.db.entity.Day

//indices means speed up query for a specific index
//and for which index speed up is date

//unique means prevent two rows having same date
@Entity(tableName = "future_weather", indices = [Index(value = ["date"], unique = true)])
data class FutureWeatherEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val date: String,
    @Embedded
    val day: Day
)