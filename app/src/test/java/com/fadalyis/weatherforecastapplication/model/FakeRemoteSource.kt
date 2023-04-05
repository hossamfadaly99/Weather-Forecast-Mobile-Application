package com.fadalyis.weatherforecastapplication.model

import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import com.fadalyis.weatherforecastapplication.network.RemoteSource

class FakeRemoteSource(var currentResponse: CurrentResponse): RemoteSource {
    override suspend fun getCurrentWeatherOnline(
        lat: String,
        lon: String,
        lang: String,
        units: String
    ): CurrentResponse {
        return currentResponse
    }
}