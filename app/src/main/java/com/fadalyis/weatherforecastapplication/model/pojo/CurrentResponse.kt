package com.fadalyis.weatherforecastapplication.model.pojo

import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.fadalyis.weatherforecastapplication.utils.AlertConverters
import com.fadalyis.weatherforecastapplication.utils.CurrentConverters
import com.fadalyis.weatherforecastapplication.utils.DailyConverters
import com.fadalyis.weatherforecastapplication.utils.HourlyConverters

@Entity(tableName = "WeatherTable")
data class CurrentResponse(
    @PrimaryKey
    val lat: String,
    val lon: String,
    val timezone: String,
    val timezone_offset: String,
    @TypeConverters(CurrentConverters::class)
    val current: Current,
    @TypeConverters(HourlyConverters::class)
    val hourly: List<Hourly>,
    @TypeConverters(DailyConverters::class)
    val daily: List<Daily>,
    @TypeConverters(AlertConverters::class)
    val alerts: List<Alert>?
)