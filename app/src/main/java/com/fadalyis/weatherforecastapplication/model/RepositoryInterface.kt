package com.fadalyis.weatherforecastapplication.model

import android.location.Address
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getCurrentWeatherOnline(
        lat: String,
        lon: String,
        lang: String,
        units: String
    ) //network

    suspend fun insertCurrentWeather(weatherResponse: CurrentResponse)
    suspend fun getCurrentWeatherOffline(): Flow<CurrentResponse?> //database
    suspend fun insertFavLocation(address: FavAddress)
    suspend fun deleteFavLocation(address: FavAddress)
    suspend fun getFavLocations(): Flow<List<FavAddress>>

    //maybe alerts retrieved/stored
}