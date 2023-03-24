package com.fadalyis.weatherforecastapplication.network

import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import kotlinx.coroutines.flow.Flow

interface RemoteSource {
    suspend fun getCurrentWeatherOnline(
        lat: String,
        lon: String,
        lang: String
    ): CurrentResponse
}