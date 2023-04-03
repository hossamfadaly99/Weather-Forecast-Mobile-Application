package com.fadalyis.weatherforecastapplication.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fadalyis.weatherforecastapplication.model.pojo.AlertSchedule
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import com.fadalyis.weatherforecastapplication.utils.AlertConverters
import com.fadalyis.weatherforecastapplication.utils.CurrentConverters
import com.fadalyis.weatherforecastapplication.utils.DailyConverters
import com.fadalyis.weatherforecastapplication.utils.HourlyConverters

@Database(entities = [CurrentResponse::class, FavAddress::class, AlertSchedule::class], version = 1)
@TypeConverters(DailyConverters::class, CurrentConverters::class, HourlyConverters::class, AlertConverters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDAO
    abstract fun getFavoriteDao(): FavoriteDAO
    abstract fun getAlertDao(): AlertDAO

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