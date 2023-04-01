package com.fadalyis.weatherforecastapplication.model.pojo

data class Alert(
    val description: String,
    val end: String,
    val event: String,
    val sender_name: String,
    val start: String,
    val tags: List<String>
)