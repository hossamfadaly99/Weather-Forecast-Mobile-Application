package com.fadalyis.weatherforecastapplication.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AlertTable")
data class AlertSchedule(
    @PrimaryKey
    val id: String,
    val startDate: Long,
    val endDate: Long,
    val type: String
)
