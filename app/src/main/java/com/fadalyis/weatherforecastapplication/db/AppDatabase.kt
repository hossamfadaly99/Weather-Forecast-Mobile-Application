package com.fadalyis.weatherforecastapplication.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import com.fadalyis.weatherforecastapplication.utils.CurrentConverters
import com.fadalyis.weatherforecastapplication.utils.DailyConverters
import com.fadalyis.weatherforecastapplication.utils.HourlyConverters

@Database(entities = [CurrentResponse::class], version = 1)
@TypeConverters(DailyConverters::class, CurrentConverters::class, HourlyConverters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDataBase::class.java, "weather_database"
                ).build()
                INSTANCE = instance
                //return instance
                instance
            }
        }
    }
}