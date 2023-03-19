package com.fadalyis.weatherforecastapplication.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

import com.fadalyis.weatherforecastapplication.utils.CurrentConverters
import com.fadalyis.weatherforecastapplication.utils.DailyConverters
import com.fadalyis.weatherforecastapplication.utils.HourlyConverters

@Entity(tableName = "WeatherTable")
data class CurrentResponse(
    @TypeConverters(CurrentConverters::class)
    val current: Current,
    @TypeConverters(DailyConverters::class)
    val daily: List<Daily>,
    @TypeConverters(HourlyConverters::class)
    val hourly: List<Hourly>,
    @PrimaryKey
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int
)