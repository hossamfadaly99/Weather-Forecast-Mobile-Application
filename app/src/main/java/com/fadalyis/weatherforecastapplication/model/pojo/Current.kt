package com.fadalyis.weatherforecastapplication.model.pojo

data class Current(
    val clouds: Int,
    val dew_point: Double,
    val dt: String,
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    val sunrise: String,
    val sunset: String,
    val temp: Double,
    val uvi: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind_deg: Int,
    val wind_gust: Double,
    val wind_speed: Double
)