package com.fadalyis.weatherforecastapplication.model

import android.location.Address
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.fadalyis.weatherforecastapplication.model.pojo.AlertSchedule
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import kotlinx.coroutines.flow.Flow
import java.util.*

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
    suspend fun getAlerts(): Flow<List<AlertSchedule>>
    suspend fun insertAlert(alert: AlertSchedule)
    suspend fun deleteAlert(id: String)
}