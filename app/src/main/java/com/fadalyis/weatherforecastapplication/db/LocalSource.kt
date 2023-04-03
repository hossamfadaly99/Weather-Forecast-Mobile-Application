package com.fadalyis.weatherforecastapplication.db

import android.location.Address
import com.fadalyis.weatherforecastapplication.model.pojo.AlertSchedule
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import com.fadalyis.weatherforecastapplication.model.pojo.LocationData
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface LocalSource {
    suspend fun insertCurrentWeather(currentWeather: CurrentResponse)
    suspend fun getCurrentWeather(): Flow<CurrentResponse?>
    suspend fun insertFavLocation(address: FavAddress)
    suspend fun deleteFavLocation(address: FavAddress)
    suspend fun getFavLocations(): Flow<List<FavAddress>>
    suspend fun insertAlert(alert: AlertSchedule)
    suspend fun deleteAlert(id: String)
    suspend fun getFAlerts(): Flow<List<AlertSchedule>>

}