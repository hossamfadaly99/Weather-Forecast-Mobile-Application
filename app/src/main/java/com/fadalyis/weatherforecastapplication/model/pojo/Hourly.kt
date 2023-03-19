package com.fadalyis.weatherforecastapplication.model.pojo

data class Hourly(
    val clouds: String,
    val dew_poString: Double,
    val dt: String,
    val feels_like: Double,
    val humidity: String,
    val pop: String,
    val pressure: String,
    val temp: Double,
    val uvi: Double,
    val visibility: String,
    val weather: List<Weather>,
    val wind_deg: String,
    val wind_gust: Double,
    val wind_speed: Double
)