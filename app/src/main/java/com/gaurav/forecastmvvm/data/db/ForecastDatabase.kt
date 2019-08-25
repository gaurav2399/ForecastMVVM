package com.gaurav.forecastmvvm.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gaurav.forecastmvvm.data.db.entity.CurrentWeatherEntry
import com.gaurav.forecastmvvm.data.db.entity.FutureWeatherEntry
import com.gaurav.forecastmvvm.data.db.entity.WeatherLocation


//entity simply means table
@Database(
    entities = [CurrentWeatherEntry::class,FutureWeatherEntry::class, WeatherLocation::class],
    version = 1
)
@TypeConverters(LocalDateConverter::class)
abstract class ForecastDatabase : RoomDatabase(){
    abstract fun currentWeatherDao(): CurrentWeatherDao
    abstract fun futureWeatherDao(): FutureWeatherDao
    abstract fun weatherLocationDao(): WeatherLocationDao

    //as we need singleton - only one database
    companion object{
        @Volatile private var instance: ForecastDatabase? = null
        //so no two objects can work concurrently on same db
        //as it create duplication
        private val LOCK = Any()

        // on invoke check that instance created or not if not then
        //call lock thread
        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
            // again confirmation
            instance?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                ForecastDatabase::class.java,"forecast.db")
                .build()
    }
}