package com.fadalyis.weatherforecastapplication.db

import android.location.Address
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import com.fadalyis.weatherforecastapplication.model.pojo.LocationData
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    suspend fun insertCurrentWeather(currentWeather: CurrentResponse)
    suspend fun getCurrentWeather(): Flow<CurrentResponse?>
    suspend fun insertFavLocation(address: FavAddress)
    suspend fun deleteFavLocation(address: FavAddress)
    suspend fun getFavLocations(): Flow<List<FavAddress>>

}