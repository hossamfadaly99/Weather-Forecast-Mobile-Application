package com.fadalyis.weatherforecastapplication.model

import android.location.Address
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getCurrentWeatherOnline(
        lat: String,
        lon: String,
        apiKey: String = "d7b359e69914f81117abea49314510cf"
    ) //network

    suspend fun insertCurrentWeather(weatherResponse: CurrentResponse)
    suspend fun getCurrentWeatherOffline(): Flow<CurrentResponse?> //database
    suspend fun insertFavLocation(address: Address)
    suspend fun deleteFavLocation(address: Address)
    suspend fun getFavLocations(): List<Address>

    //maybe alerts retrieved/stored
}