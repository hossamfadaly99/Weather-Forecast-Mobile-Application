package com.fadalyis.weatherforecastapplication.model.pojo

data class Daily(
    val clouds: String,
    val dew_poString: Double,
    val dt: String,
    val feels_like: FeelsLike,
    val humidity: String,
    val moon_phase: Double,
    val moonrise: String,
    val moonset: String,
    val pop: Double,
    val pressure: String,
    val rain: Double,
    val sunrise: String,
    val sunset: String,
    val temp: Temp,
    val uvi: Double,
    val weather: List<Weather>,
    val wind_deg: String,
    val wind_gust: Double,
    val wind_speed: Double
)